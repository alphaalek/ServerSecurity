package me.alek.serversecurity.command.subcommands;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.command.SubCommandImpl;
import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.malware.scanning.VulnerabilityScanner;
import me.alek.serversecurity.utils.JARFinder;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class ReloadCommand implements SubCommandImpl {
    @Override
    public boolean perform(CommandSender sender, String label, String[] args) {
        ServerSecurityPlugin.get().getConfiguration().reload();

        File dataFolder = ServerSecurityPlugin.get().getDataFolder().getParentFile();
        sender.sendMessage(Lang.getMessageWithPrefix(Lang.SUBCOMMAND_RELOAD_RELOADING));

        VulnerabilityScanner scanner = VulnerabilityScanner.latestScanner;
        if (scanner == null || !scanner.isScanning()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    new VulnerabilityScanner(JARFinder.findAllJars(dataFolder)).startScan(null);
                }
            }.runTaskAsynchronously(ServerSecurityPlugin.get());
        }
        return true;
    }

    @Override
    public boolean executableByConsole() {
        return true;
    }

    @Override
    public String getUsage() {
        return "/am reload";
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return Lang.getMessage(Lang.SUBCOMMAND_RELOAD_INFO_DESCRIPTION);
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
