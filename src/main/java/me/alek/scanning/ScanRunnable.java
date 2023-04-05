package me.alek.scanning;

import me.alek.AntiMalwarePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ScanRunnable implements Runnable {

    private final ScanService service;

    public ScanRunnable(ScanService service) {
        this.service = service;
    }

    /*
     * Lavet en timer task runnable for at undgå lag. TPS kunne komme under 10 før, uden dette.
     * Nu bliver den typisk over 18+ (altså medmindre der er andre faktorer som driver den gevaldigt ned)
     */

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
        }.runTaskTimerAsynchronously(AntiMalwarePlugin.getInstance(), 0L, 10L);

    }
}
