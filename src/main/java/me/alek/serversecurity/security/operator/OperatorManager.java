package me.alek.serversecurity.security.operator;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.security.SecurityManager;
import me.alek.serversecurity.utils.AsynchronousTask;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OperatorManager {

    private static OperatorManager instance;
    private final SecurityManager manager;

    public static synchronized OperatorManager get() {
        if (instance == null) {
            instance = new OperatorManager(SecurityManager.getInstance());
        }
        return instance;
    }

    private final ArrayList<String> allowedOpPlayers = new ArrayList<>();
    private final ArrayList<OpPlayerChange> opPlayerChanges = new ArrayList<>();

    private OperatorManager(SecurityManager manager) {
        new GarbageRemoverTask().runAsync();
        this.manager = manager;
    }

    public void setAllowedOpPlayers(List<String> players) {
        allowedOpPlayers.clear();
        this.allowedOpPlayers.addAll(players);
    }

    public boolean isPlayerAllowed(OfflinePlayer player) {
        boolean isOpProxyEnabled = ServerSecurityPlugin.get().getConfiguration().getOptions().isOpProxyBlockerEnabled();

        if (!isOpProxyEnabled) return true;
        return this.allowedOpPlayers.contains(player.getName()) || this.allowedOpPlayers.contains(player.getUniqueId().toString());
    }

    public void put(OfflinePlayer player, boolean isOp) {
        new ArrayList<>(opPlayerChanges)
                .stream()
                .filter(change -> change.getPlayer().getName().equals(player.getName()))
                .forEach(opPlayerChanges::remove);
        Instant instant = Instant.now();

        opPlayerChanges.add(new OpPlayerChange() {
            @Override
            public OfflinePlayer getPlayer() {
                return player;
            }

            @Override
            public Instant getInstant() {
                return instant;
            }

            @Override
            public boolean isOp() {
                return isOp;
            }
        });
    }

    public List<OpPlayerChange> getLatestOpChanges(Duration duration) {
        final Instant now = Instant.now();
        final List<OpPlayerChange> latestOpChanges = new ArrayList<>();
        opPlayerChanges
                .stream()
                .filter(change -> isNewerThan(change.getInstant(), now, duration))
                .forEach(latestOpChanges::add);
        return latestOpChanges;
    }

    private boolean isOlderThan(Instant instant1, Instant instant2, Duration duration) {
        return Duration.between(instant1, instant2).compareTo(duration) > 0;
    }

    private boolean isNewerThan(Instant instant1, Instant instant2, Duration duration) {
        return Duration.between(instant1, instant2).compareTo(duration) < 0;
    }

    public void removeOld() {
        final Instant now = Instant.now();
        opPlayerChanges
                .stream()
                .filter(change -> isOlderThan(change.getInstant(), now, Duration.ofMinutes(30)))
                .forEach(opPlayerChanges::remove);
    }

    public interface OpPlayerChange {

        OfflinePlayer getPlayer();

        Instant getInstant();

        boolean isOp();
    }

    private class GarbageRemoverTask extends AsynchronousTask {

        @Override
        public BukkitRunnable getRunnable() {
            return new BukkitRunnable() {

                @Override
                public void run() {
                    removeOld();
                }
            };
        }

        @Override
        public long getPeriod() {
            return 18000L;
        }
    }

}
