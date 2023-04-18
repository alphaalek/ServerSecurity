package me.alek.security;

import lombok.Getter;
import me.alek.AntiMalwarePlugin;
import me.alek.security.config.ResourceProviderWrapper;
import me.alek.security.config.SecurityConfig;
import me.alek.security.event.ListenerRegistery;
import me.alek.security.event.wrappers.WrappedPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class SecurityManager {

    @Getter private final AntiMalwarePlugin plugin;
    @Getter private final ResourceProviderWrapper resourceProvider;
    @Getter private final SecurityConfig securityConfig;
    @Getter private SecurityOptions options;

    public SecurityManager(AntiMalwarePlugin plugin) {
        this.plugin = plugin;
        this.resourceProvider = new ResourceProviderWrapper(plugin);
        this.securityConfig = new SecurityConfig(this);

        init();
    }

    public void reload() {
        this.securityConfig.reload();
        generatePluginOptions();
    }

    private void init() {
        generatePluginOptions();
        if (this.options.isEnabled()) {
            //if (options.isPreventCancelledMaliciousChatEvents()) injectPluginManager();
        }
        new ListenerRegistery(this);
    }

    public void generatePluginOptions() {
        this.options = new SecurityOptions(this.securityConfig);
    }


    private static void injectPluginManager() {
        try {
            Server server = Bukkit.getServer();
            Field pluginManagerField = server.getClass().getDeclaredField("pluginManager");
            pluginManagerField.setAccessible(true);
            PluginManager originalPluginManager = (PluginManager) pluginManagerField.get(server);
            WrappedPluginManager customPluginManager = new WrappedPluginManager(originalPluginManager);
            pluginManagerField.set(server, customPluginManager);
        } catch(NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
}
