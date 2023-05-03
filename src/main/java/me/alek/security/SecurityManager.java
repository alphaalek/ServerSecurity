package me.alek.security;

import lombok.Getter;
import me.alek.AntiMalwarePlugin;
import me.alek.security.blocker.ListenerRegistery;
import me.alek.security.blocker.wrappers.WrappedPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;

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
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (this.options.isEnabled()) {
            try {
                // plugin manager
                PluginManager wrappedPluginManager = new WrappedPluginManager(Bukkit.getPluginManager());
                Field pluginManagerField = ((CraftServer)Bukkit.getServer()).getClass().getDeclaredField("pluginManager");
                pluginManagerField.setAccessible(true);
                pluginManagerField.set(Bukkit.getServer(), wrappedPluginManager);

                // INJECT KNOWN COMMANDS I COMMAND MAP MED WRAPPED COMMAND SOM INTERCEPTOR VED CONSOLE EXECUTION, OG SÅ GØR DET SAMME SOM FØR MED RESPONSELISTENER OSV
                Class<?> commandMapClass = ((CraftServer) Bukkit.getServer()).getCommandMap().getClass();
                Field knownCommandsField = commandMapClass.getField("knownCommands");
                knownCommandsField.setAccessible(true);

            } catch (NoSuchFieldException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        new ListenerRegistery(this, pluginManager);
    }



}
