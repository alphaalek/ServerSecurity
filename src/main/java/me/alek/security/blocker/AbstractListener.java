package me.alek.security.blocker;

import lombok.Getter;
import me.alek.security.SecurityManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public abstract class AbstractListener implements Listener {

    @Getter private final SecurityManager manager;
    @Getter private final PluginManager pluginManager;

    public AbstractListener(SecurityManager manager, PluginManager pluginManager) {
        this.manager = manager;
        this.pluginManager = pluginManager;
    }
}
