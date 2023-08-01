package me.alek.serversecurity.security.blocker;

import me.alek.serversecurity.security.SecurityManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public abstract class AbstractListener implements Listener {

    private final SecurityManager manager;
    private final PluginManager pluginManager;

    public AbstractListener(SecurityManager manager, PluginManager pluginManager) {
        this.manager = manager;
        this.pluginManager = pluginManager;
    }

    public SecurityManager getManager() {
        return manager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }
}
