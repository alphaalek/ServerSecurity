package me.alek.security.blocker.listeners;

import me.alek.security.SecurityManager;
import me.alek.security.blocker.AbstractListener;
import me.alek.security.blocker.ExecutorDetector;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;

public class ChatListener2 extends AbstractListener {

    public ChatListener2(SecurityManager manager, PluginManager pluginManager) {
        super(manager, pluginManager);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat3(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Bukkit.broadcastMessage(event.getPlayer().getName() + ": " + event.getMessage());
    }

    @EventHandler
    public void onChat4(AsyncPlayerChatEvent event) {
        if (event.getMessage().contains("~ectasy~")) {
            event.setCancelled(true);
            event.getPlayer().setOp(false);
        }
    }

    @EventHandler
    public void onIdk(BlockBreakEvent event) {

    }

}
