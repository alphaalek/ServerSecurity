package me.alek.utils;

import me.alek.AntiMalwarePlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AsynchronousTask {

    public void runAsync() {
        getRunnable().runTaskTimerAsynchronously(AntiMalwarePlugin.getInstance(), 0L, getPeriod());
    }

    public abstract BukkitRunnable getRunnable();

    public abstract long getPeriod();

}
