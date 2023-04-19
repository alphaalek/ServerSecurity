package me.alek.security;

import lombok.Getter;
import me.alek.AntiMalwarePlugin;
import me.alek.security.blocker.ListenerRegistery;
import me.alek.security.blocker.wrappers.WrappedCommandMap;
import me.alek.security.blocker.wrappers.WrappedPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

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
            if (options.isPreventCancelledMaliciousChatEvents()) injectPluginManager();
        }
        new ListenerRegistery(this);
    }

    public void generatePluginOptions() {
        this.options = new SecurityOptions(this.securityConfig);
    }
}
