package me.alek.security.blocker.listeners;

import me.alek.security.SecurityManager;
import me.alek.security.blocker.AbstractListener;
import org.bukkit.plugin.PluginManager;

public class PlayerKickListener extends AbstractListener {

    public PlayerKickListener(SecurityManager manager, PluginManager pluginManager) {
        super(manager, pluginManager);
    }


}
