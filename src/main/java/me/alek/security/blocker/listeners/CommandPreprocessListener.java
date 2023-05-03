package me.alek.security.blocker.listeners;

import me.alek.security.blocker.AbstractListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import me.alek.security.SecurityManager;
import org.bukkit.plugin.PluginManager;

public final class CommandPreprocessListener extends AbstractListener {

    public CommandPreprocessListener(SecurityManager manager, PluginManager pluginManager) {
        super(manager, pluginManager);
    }

    @EventHandler
    public void preprocessEvent(PlayerCommandPreprocessEvent event) {
        if (super.getManager().getOptions().isEnabled()) {
            event.getPlayer().sendMessage("is enabled");
        }
    }
}
