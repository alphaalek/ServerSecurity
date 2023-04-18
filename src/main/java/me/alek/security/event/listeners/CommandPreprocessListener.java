package me.alek.security.event.listeners;

import me.alek.security.event.AbstractListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import me.alek.security.SecurityManager;

public final class CommandPreprocessListener extends AbstractListener {

    public CommandPreprocessListener(SecurityManager manager) {
        super(manager);
    }

    @EventHandler
    public void preprocessEvent(PlayerCommandPreprocessEvent event) {
        if (super.getManager().getOptions().isEnabled()) {
            event.getPlayer().sendMessage("is enabled");
        }
    }
}
