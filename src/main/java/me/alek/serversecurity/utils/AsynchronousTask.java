package me.alek.serversecurity.utils;

import me.alek.serversecurity.ServerSecurityPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AsynchronousTask {

    public void runAsync() {
        getRunnable().runTaskTimerAsynchronously(ServerSecurityPlugin.get(), 0L, getPeriod());
    }

    public abstract BukkitRunnable getRunnable();

    public abstract long getPeriod();

}
