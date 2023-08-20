package me.alek.serversecurity;

import me.alek.serversecurity.configuration.Configuration;
import me.alek.serversecurity.command.commands.MainCommand;
import me.alek.serversecurity.logging.AbstractLogger;
import me.alek.serversecurity.logging.LogHolder;
import me.alek.serversecurity.malware.scanning.VulnerabilityScanner;
import me.alek.serversecurity.metrics.Metrics;
import me.alek.serversecurity.network.NetworkHandler;
import me.alek.serversecurity.security.SecurityManager;
import me.alek.serversecurity.utils.Appender;
import me.alek.serversecurity.utils.JARFinder;
import me.alek.serversecurity.utils.UpdateChecker;
import me.alek.serversecurity.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ServerSecurityPlugin extends JavaPlugin implements Listener {

    private static ServerSecurityPlugin instance;
    private static SecurityManager securityManager;
    private static Configuration configuration;
    private static NetworkHandler interceptor;
    private static String latestVersion = null;

    @Override
    public void onEnable() {
        instance = this;

        // setup configuration
        configuration = new Configuration();

        // setup security manager
        securityManager = new SecurityManager(this);

        // setup network interceptor
        interceptor = new NetworkHandler(this);
        interceptor.enable();

        // check latest updates
        new UpdateChecker(this, 109025).getLatestVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {

                this.getLogger().info(Lang.getMessageFormatted(Lang.UPDATE_FOUND, version));
                this.getLogger().info("https://www.spigotmc.org/resources/serversecurity.109025/");

                latestVersion = version;
            }
        });
        // setup metrics
        Metrics metrics = new Metrics(this, 18393);

        // setup commands
        this.getCommand("am").setExecutor(new MainCommand());

        // setup listeners
        this.getServer().getPluginManager().registerEvents(this, this);

        this.getLogger().info("ServerSecurity is now loaded in! The plugins is made by Alek05. Use /am.");

        // scan the plugins of the server
        new BukkitRunnable() {
            @Override
            public void run() {

                final File dataFolder = ServerSecurityPlugin.get().getDataFolder().getParentFile();
                final VulnerabilityScanner scanner = new VulnerabilityScanner(JARFinder.findAllJars(dataFolder), false);

                final Appender whenDone = new Appender();
                whenDone.setRunnable(() -> {

                    if (scanner.hasMalware()) {

                        Bukkit.getOnlinePlayers()
                                .stream()
                                .filter(Player::isOp)
                                .forEach(player -> player.sendMessage(Lang.getMessageWithPrefix(Lang.SCANNING_WARN_INFECTED_JOIN)));
                    }
                });
                scanner.startScan(whenDone);
            }
        }.runTaskTimerAsynchronously(this, 0L, 36000L);
    }

    @Override
    public void onDisable() {
        interceptor.disable();

        disableLoggerContexts();
    }

    public void disableLoggerContexts() {
        List<AbstractLogger> loggers = Arrays.asList(
                LogHolder.getOPLogger(), LogHolder.getSecurityLogger(), LogHolder.getScanLogger()
        );
        for (AbstractLogger logger : loggers) {
            if (logger != null) {

                logger.getContext().stop();
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) return;

        if (latestVersion != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage(Lang.getMessageFormattedWithPrefix(Lang.UPDATE_FOUND, latestVersion));
                    player.sendMessage("§ahttps://www.spigotmc.org/resources/serversecurity.109025/");
                }
            }.runTaskLater(this, 60L);
        }

        VulnerabilityScanner latestScanner = VulnerabilityScanner.latestScanner;
        if (latestScanner != null && latestScanner.hasMalware()) {
            player.sendMessage(Lang.getMessageWithPrefix(Lang.SCANNING_WARN_INFECTED_JOIN));
        }
    }

    public static ServerSecurityPlugin get() {
        return instance;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

}

