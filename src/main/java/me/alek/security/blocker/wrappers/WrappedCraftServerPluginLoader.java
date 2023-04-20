package me.alek.security.blocker.wrappers;

import me.alek.AntiMalwarePlugin;
import me.alek.security.blocker.CustomClassLoader;
import me.alek.security.blocker.ServerClassVisitor;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public class WrappedCraftServerPluginLoader implements PluginLoader {

    private final JavaPluginLoader loader;

    public WrappedCraftServerPluginLoader() {
        this.loader = (JavaPluginLoader) AntiMalwarePlugin.getInstance().getPluginLoader();
    }

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException {
        if (file.getName().endsWith(".jar")) {
            try {
                // Load the modified CraftServer class
                byte[] modifiedCraftServerBytes = modifyServerClass();
                CustomClassLoader classLoader = new CustomClassLoader(getClass().getClassLoader());
                Class<?> modifiedCraftServerClass = classLoader.defineClass("org.bukkit.craftbukkit.v1_8_R3.CraftServer", modifiedCraftServerBytes);

                // Create a new instance of the modified CraftServer class
                MinecraftServer minecraftServer = ((CraftServer)Bukkit.getServer()).getServer();
                PlayerList playerList = minecraftServer.getPlayerList();
                Constructor<?> craftServerConstructor = modifiedCraftServerClass.getConstructor(MinecraftServer.class, PlayerList.class);
                CraftServer modifiedCraftServer = (CraftServer) craftServerConstructor.newInstance(minecraftServer, playerList);

                // Set the modified CraftServer instance in the Bukkit class
                Field serverField = Bukkit.class.getDeclaredField("server");
                serverField.setAccessible(true);
                serverField.set(null, modifiedCraftServer);

                // Replace the command map with a wrapped command map
                CommandMap defaultCommandMap = modifiedCraftServer.getCommandMap();
                WrappedCommandMap wrappedCommandMap = new WrappedCommandMap(defaultCommandMap);
                Field commandMapField = modifiedCraftServer.getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMapField.set(modifiedCraftServer, wrappedCommandMap);

                // Load the plugin using the delegate loader
                return loader.loadPlugin(file);
            } catch (Exception e) {
                throw new InvalidPluginException(e);
            }
        } else {
            throw new InvalidPluginException("Plugin file must be a jar");
        }
    }

    private byte[] modifyServerClass() throws IOException {

        InputStream inputStream = CraftServer.class.getResourceAsStream("/org/bukkit/craftbukkit/v1_8_R3/CraftServer.class");
        ClassReader classReader = new ClassReader(inputStream);

        ClassWriter classWriter = new ClassWriter(classReader, 0);

        ServerClassVisitor serverClassVisitor = new ServerClassVisitor(classWriter);
        classReader.accept(serverClassVisitor, 0);

        return classWriter.toByteArray();
    }

    @Override
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin plugin) {
        return loader.createRegisteredListeners(listener, plugin);
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        loader.enablePlugin(plugin);
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        loader.disablePlugin(plugin);
    }

    @Override
    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        return loader.getPluginDescription(file);
    }

    @Override
    public Pattern[] getPluginFileFilters() {
        return loader.getPluginFileFilters();
    }
}
