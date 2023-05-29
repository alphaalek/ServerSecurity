package me.alek.security;

import lombok.Getter;
import me.alek.AntiMalwarePlugin;
import me.alek.logging.LogHolder;
import me.alek.security.blocker.ListenerRegistery;
import me.alek.security.blocker.wrappers.WrappedPluginManager;
import me.alek.security.operator.OperatorInjector;
import me.alek.security.operator.OperatorManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class SecurityManager {

    @Getter
    private final AntiMalwarePlugin plugin;

    private final SecurityConfig securityConfig;
    @Getter
    private SecurityOptions options;

    public SecurityManager(AntiMalwarePlugin plugin) {
        this.plugin = plugin;
        this.securityConfig = new SecurityConfig(this);

        init();
    }

    public void generatePluginOptions() {
        final SecurityOptions options = new SecurityOptions(this.securityConfig);
        this.options = options;
        LogHolder.setup(options);
    }

    public void reload() {
        this.securityConfig.reload();
        AntiMalwarePlugin.getInstance().disableLoggingHandlers();
        new BukkitRunnable() {

            @Override
            public void run() {
                generatePluginOptions();
            }
        }.runTaskLater(AntiMalwarePlugin.getInstance(), 10L);
    }

    private void init() {
        generatePluginOptions();
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (this.options.isPreventCancelledMaliciousChatEvents()) {
            try {
                PluginManager wrappedPluginManager = new WrappedPluginManager(Bukkit.getPluginManager());
                Field pluginManagerField = ((CraftServer)Bukkit.getServer()).getClass().getDeclaredField("pluginManager");
                pluginManagerField.setAccessible(true);
                pluginManagerField.set(Bukkit.getServer(), wrappedPluginManager);

                OperatorInjector operatorInjector = new OperatorInjector(OperatorManager.get());
                operatorInjector.inject();

            } catch (NoSuchFieldException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        new ListenerRegistery(this, pluginManager);
    }



}
