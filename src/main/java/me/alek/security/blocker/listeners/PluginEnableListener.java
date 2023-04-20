package me.alek.security.blocker.listeners;

import me.alek.security.SecurityManager;
import me.alek.security.blocker.AbstractListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginEnableEvent;

public class PluginEnableListener extends AbstractListener {

    private final SecurityManager manager;

    public PluginEnableListener(SecurityManager manager) {
        super(manager);
        this.manager = manager;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {

    }
}
