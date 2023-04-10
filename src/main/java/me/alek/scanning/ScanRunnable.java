package me.alek.scanning;

import me.alek.AntiMalwarePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ScanRunnable implements Runnable {

    private final ScanService service;
    private ScanStatus status;

    public ScanRunnable(ScanService service) {
        this.service = service;
    }

    @Override
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                ScanStatus.State state;
                if (status == null) {
                    state = ScanStatus.State.UNKNOWN;
                } else {
                    state = status.getState();
                }
                if (state == ScanStatus.State.SCANNING) return;
                if (!service.hasMore()) {
                    this.cancel();
                } else if (state == ScanStatus.State.UNKNOWN || state == ScanStatus.State.DONE) {
                    status = service.start();
                    service.execute(status);
                }
            }
        }.runTaskTimerAsynchronously(AntiMalwarePlugin.getInstance(), 0L, 15L);

    }
}
