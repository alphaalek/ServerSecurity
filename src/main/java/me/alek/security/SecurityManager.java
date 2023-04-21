package me.alek.security;

import lombok.Getter;
import me.alek.AntiMalwarePlugin;
import me.alek.security.blocker.CustomClassLoader;
import me.alek.security.blocker.ListenerRegistery;
import me.alek.security.blocker.visitors.BukkitClassVisitor;
import me.alek.security.blocker.visitors.ServerClassVisitor;
import me.alek.security.blocker.listeners.test;
import me.alek.security.blocker.wrappers.WrappedCommandMap;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.*;
import java.util.stream.Collectors;

public class SecurityManager {

    @Getter
    private final AntiMalwarePlugin plugin;
    @Getter
    private final ResourceProviderWrapper resourceProvider;
    @Getter
    private final SecurityConfig securityConfig;
    @Getter
    private SecurityOptions options;

    private final String CLASS_NAME = "org.bukkit.craftbukkit.v1_8_R3.CraftServer";
    private final String CLASS_PATH = "/org/bukkit/craftbukkit/v1_8_R3/CraftServer.class";

    public SecurityManager(AntiMalwarePlugin plugin) {
        this.plugin = plugin;
        this.resourceProvider = new ResourceProviderWrapper(plugin);
        this.securityConfig = new SecurityConfig(this);

        init();
    }

    public void generatePluginOptions() {
        this.options = new SecurityOptions(this.securityConfig);
    }

    public void reload() {
        this.securityConfig.reload();
        generatePluginOptions();
    }

    private void init() {
        generatePluginOptions();
        if (this.options.isEnabled()) {
            injectServer();
        }
        new ListenerRegistery(this);
        AntiMalwarePlugin.getInstance().getCommand("test").setExecutor(new test());
    }

    public void injectServer() {
        if (!options.isPreventCancelledMaliciousChatEvents()) {
            Bukkit.broadcastMessage("returning");
            return;
        }
        try {
            // instances
            Server server = Bukkit.getServer();
            CraftServer craftServerInstance = (CraftServer) server;

            // command map field get
            Field commandMapField = craftServerInstance.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap defaultCommandMap = (CommandMap) commandMapField.get(craftServerInstance);
            WrappedCommandMap wrappedCommandMap = new WrappedCommandMap(defaultCommandMap);

            // load & modify craft server class
            byte[] modifiedClassBytes = modifyServerClass();
            CustomClassLoader customClassLoader = new CustomClassLoader(CLASS_NAME, modifiedClassBytes);
            Class<?> modifiedClass = customClassLoader.loadClass(CLASS_NAME);

            // fjerner singleton server check fra bukkit
            if (!modifyBukkitInServerJar()) {
                // TODO: DISALE
                System.out.println("disabling...");
                return;
            }

            // ny server instance
            MinecraftServer minecraftServer = craftServerInstance.getServer();
            Constructor<?> craftServerConstructor = modifiedClass.getDeclaredConstructor(MinecraftServer.class, PlayerList.class);
            craftServerConstructor.setAccessible(true);
            Object modifiedCraftServerInstance = craftServerConstructor.newInstance(minecraftServer, minecraftServer.getPlayerList());

            // command map field i ny server instance
            Field field = modifiedClass.getDeclaredField("commandMap");
            field.setAccessible(true);
            field.set(modifiedCraftServerInstance, wrappedCommandMap);

            // bukkit server field til ny server instance
            Field bukkitServerField = Bukkit.class.getDeclaredField("server");
            bukkitServerField.setAccessible(true);
            bukkitServerField.set(null, modifiedCraftServerInstance);

        } catch (NoSuchFieldException | IllegalAccessException | IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    private File getRightJar(List<File> files) {
        if (files.size() == 1) {
            return files.get(0);
        }
        File ifNoServerJar = null;
        for (File file : files) {
            if (file.getName().toLowerCase().contains("server")) {
                return file;
            }
            String lower = file.getName().toLowerCase();
            if (lower.contains("spigot") || lower.contains("bukkit") || lower.contains("paper")) {
                ifNoServerJar = file;
            }
        }
        return ifNoServerJar;
    }

    private File getServerJar() {
        File serverFolder = Bukkit.getServer().getWorldContainer();
        if (serverFolder == null) {
            System.out.println("server null");
            return null;
        }
        File[] files = serverFolder.listFiles();
        if (files == null) {
            System.out.println("files null");
            return null;
        }
        List<File> serverJarCandidates = Arrays.stream(files)
                .filter(file -> file.getName().endsWith(".jar"))
                .collect(Collectors.toList());

        if (serverJarCandidates.isEmpty()) {
            System.out.println("jar empty");
            return null;
        }
        File rightJar = getRightJar(serverJarCandidates);
        if (rightJar == null) {
            System.out.println("jar null");
            return null;
        }
        return rightJar;
    }

    private boolean modifyBukkitInServerJar() throws IOException {
        File serverJar = getServerJar();
        if (serverJar == null) {
            System.out.println("jar null");
            return false;
        }
        // Read the original class bytes from the server.jar file
        JarFile jarFile = new JarFile(serverJar);
        InputStream inputStream;
        byte[] originalClassBytes = null;
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().equals("org/bukkit/Bukkit.class")) {
                inputStream = jarFile.getInputStream(entry);
                originalClassBytes = IOUtils.toByteArray(inputStream);
                inputStream.close();
                break;
            }
        }
        if (originalClassBytes == null) {
            jarFile.close();
            System.out.println("Bukkit class not found in server jar");
            return false;
        }

        // Modify the class bytes using ASM
        ClassReader classReader = new ClassReader(originalClassBytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new BukkitClassVisitor(Opcodes.ASM9, classWriter);
        classReader.accept(classVisitor, 0);
        byte[] modifiedClassBytes = classWriter.toByteArray();

        // Write the modified class bytes back to the server.jar file
        OutputStream outputStream = new FileOutputStream(serverJar);
        Manifest manifest = jarFile.getManifest();
        if (manifest == null) {
            manifest = new Manifest();
        }
        JarOutputStream jarOutputStream = new JarOutputStream(outputStream, manifest);
        JarEntry modifiedEntry = new JarEntry("org/bukkit/Bukkit.class");
        jarOutputStream.putNextEntry(modifiedEntry);
        jarOutputStream.write(modifiedClassBytes);
        jarOutputStream.closeEntry();

        // Copy the rest of the entries in the original jar file to the new jar file
        entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.getName().equals("org/bukkit/Bukkit.class")) {
                jarOutputStream.putNextEntry(entry);
                inputStream = jarFile.getInputStream(entry);
                IOUtils.copy(inputStream, jarOutputStream);
                inputStream.close();
                jarOutputStream.closeEntry();
            }
        }

        // Close the input/output streams and the jar file
        jarFile.close();
        jarOutputStream.close();
        outputStream.close();

        System.out.println("Modified JAR file created");
        return true;
    }

    private byte[] readBytesFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteStream.write(buffer, 0, bytesRead);
        }
        return byteStream.toByteArray();
    }

    private byte[] modifyServerClass() throws IOException {

        InputStream inputStream = CraftServer.class.getResourceAsStream(CLASS_PATH);
        ClassReader classReader = new ClassReader(inputStream);

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ServerClassVisitor serverClassVisitor = new ServerClassVisitor(classWriter);
        classReader.accept(serverClassVisitor, 0);

        return classWriter.toByteArray();
    }

}
