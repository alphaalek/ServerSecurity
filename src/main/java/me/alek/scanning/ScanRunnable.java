package me.alek.scanning;

import me.alek.AntiMalwarePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ScanRunnable implements Runnable {

    private final ScanService service;

    public ScanRunnable(ScanService service) {
        this.service = service;
    }

    @Override
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (service.hasMore()) {
                    service.start();
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(AntiMalwarePlugin.getInstance(), 0L, 15L);

    }
}
