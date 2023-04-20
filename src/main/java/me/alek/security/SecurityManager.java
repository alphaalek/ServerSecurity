package me.alek.security;

import lombok.Getter;
import me.alek.AntiMalwarePlugin;
import me.alek.security.blocker.CustomClassLoader;
import me.alek.security.blocker.ListenerRegistery;
import me.alek.security.blocker.ServerClassVisitor;
import me.alek.security.blocker.listeners.test;
import me.alek.security.blocker.wrappers.WrappedCommandMap;
import me.alek.security.blocker.wrappers.WrappedCraftServerPluginLoader;
import net.minecraft.server.v1_8_R3.DedicatedPlayerList;
import net.minecraft.server.v1_8_R3.DedicatedServer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.PluginLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

public class SecurityManager {

    @Getter
    private final AntiMalwarePlugin plugin;
    @Getter
    private final ResourceProviderWrapper resourceProvider;
    @Getter
    private final SecurityConfig securityConfig;
    @Getter
    private SecurityOptions options;

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

        /*try {
            Object serverInstance = Bukkit.getServer();
            Class<?> craftServerClass = serverInstance.getClass();

            Field commandMapField = craftServerClass.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            SimpleCommandMap defaultCommandMap = (SimpleCommandMap) commandMapField.get(serverInstance);
            WrappedCommandMap wrappedCommandMap = new WrappedCommandMap(defaultCommandMap);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(commandMapField,commandMapField.getModifiers() & ~Modifier.FINAL);

            commandMapField.set(serverInstance, wrappedCommandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }*/

        try {
            // erstatter commandMap field med en wrapped command map

            // instances
            Server server = Bukkit.getServer();
            CraftServer craftServerInstance = (CraftServer) server;
            AntiMalwarePlugin.getInstance().getLogger().info(craftServerInstance.getClass().getName());

            // command map field get
            Field commandMapField = craftServerInstance.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap defaultCommandMap = (CommandMap) commandMapField.get(craftServerInstance);
            WrappedCommandMap wrappedCommandMap = new WrappedCommandMap(defaultCommandMap);

            // load & modify craft server class
            byte[] modifiedBytecode = modifyServerClass();
            CustomClassLoader customClassLoader = new CustomClassLoader(getClass().getClassLoader());
            Class<?> modifiedServerClass = customClassLoader.defineClass("org.bukkit.craftbukkit.v1_8_R3.CraftServer", modifiedBytecode);

            // injected command map setter invocation
            //Method setCommandMapMethod = modifiedServerClass.getMethod("setCommandMap", CommandMap.class);
            //setCommandMapMethod.invoke(modifiedCraftServerInstance, wrappedCommandMap);

            // command map field set
            Field modifiedCommandMapField = modifiedServerClass.getDeclaredField("commandMap");
            modifiedCommandMapField.setAccessible(true);
            modifiedCommandMapField.set(null, wrappedCommandMap);

            System.out.println(modifiedCommandMapField.getModifiers());

            // constructor & new instance
            Constructor<?> constructor = modifiedServerClass.getDeclaredConstructor(MinecraftServer.class, PlayerList.class);
            MinecraftServer minecraftServer = craftServerInstance.getServer();
            CraftServer modifiedCraftServerInstance = (CraftServer) constructor.newInstance(minecraftServer, minecraftServer.getPlayerList());

            Field bukkitServerField = Bukkit.class.getDeclaredField("server");
            bukkitServerField.setAccessible(true);
            bukkitServerField.set(null, modifiedCraftServerInstance);

            System.out.println("NEW VALUE " + modifiedCommandMapField.get(null));

        } catch (InvocationTargetException ex) {
            ex.getCause().printStackTrace();
        }
        catch (NoSuchFieldException | InstantiationException | IllegalAccessException | NoSuchMethodException | IOException ex) {
            ex.printStackTrace();
        }
    }

}
