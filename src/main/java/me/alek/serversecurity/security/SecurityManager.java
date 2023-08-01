package me.alek.serversecurity.security;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.configuration.ConfigurationOptions;
import me.alek.serversecurity.logging.LogHolder;
import me.alek.serversecurity.security.injector.ListenerInjector;
import me.alek.serversecurity.security.blocker.ListenerRegistery;
import me.alek.serversecurity.security.operator.OperatorInjector;
import me.alek.serversecurity.security.operator.OperatorManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class SecurityManager {

    private final ServerSecurityPlugin plugin;
    private static SecurityManager instance;

    public static SecurityManager getInstance() {
        return instance;
    }

    public SecurityManager(ServerSecurityPlugin plugin) {
        instance = this;
        this.plugin = plugin;

        LogHolder.setup();
        init();
    }

    private void init(){
        ConfigurationOptions options = plugin.getConfiguration().getOptions();

        PluginManager pluginManager = Bukkit.getPluginManager();
        if (options.isPreventCancelledMaliciousChatEvents()) {
            try {
                ListenerInjector.inject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (options.isOpProxyBlockerEnabled()) {
            try {
                OperatorInjector operatorInjector = new OperatorInjector(this, OperatorManager.get());
                operatorInjector.inject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        new ListenerRegistery(this, pluginManager);
    }


}
