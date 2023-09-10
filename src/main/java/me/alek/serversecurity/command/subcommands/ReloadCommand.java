package me.alek.serversecurity.command.subcommands;

import me.alek.serversecurity.command.SubCommandImpl;
import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.malware.scanning.JarBytecodeScanner;
import me.alek.serversecurity.malware.scanning.MalwareScanner;
import me.alek.serversecurity.utils.JARFinder;
import me.alek.serversecurity.lang.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class ReloadCommand implements SubCommandImpl {
    @Override
    public void perform(CommandSender sender, String label, String[] args) {
        ServerSecurityPlugin.get().getConfiguration().reload();

        File dataFolder = ServerSecurityPlugin.get().getDataFolder().getParentFile();
        sender.sendMessage(Lang.getMessageWithPrefix(Lang.SUBCOMMAND_RELOAD_RELOADING));

        MalwareScanner scanner = MalwareScanner.latestScanner;
        if (scanner == null || !scanner.isScanning()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    new MalwareScanner(JARFinder.findAllJars(dataFolder), false).startScan();
                }
            }.runTaskAsynchronously(ServerSecurityPlugin.get());
        }
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
