package me.alek.command.subcommands;

import me.alek.AntiMalwarePlugin;
import me.alek.command.SubCommand;
import me.alek.scanning.ScanManager;
import me.alek.scanning.Scanner;
import me.alek.utils.JARFinder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class ReloadCommand implements SubCommand {
    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Scanner scanner = ScanManager.getLatestScanner();
        if (scanner.isScanning()) {
            int size = scanner.getFiles().size();
            player.sendMessage("§8[§6AntiMalware§8] §7Serveren er allerede igang med at opdatere cache i auto-update. Vent lidt... ("
                    + (size - scanner.getService().getNotDoneFiles().size()) + "/" + size + ")");
            return;
        }
        File dataFolder = AntiMalwarePlugin.getInstance().getDataFolder().getParentFile();
        player.sendMessage("§8[§6AntiMalware§8] §7Reloader pluginnet...");
        new BukkitRunnable() {
            @Override
            public void run() {
                new Scanner(JARFinder.findAllJars(dataFolder)).startScan();
            }
        }.runTaskAsynchronously(AntiMalwarePlugin.getInstance());
    }

    @Override
    public String getUsage() {
        return "/antimalware reload";
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloader cached resultdata set, hvis du har tilføjet nye plugins du vil have scannet. (serveren gør det også automatisk hvert 10. minut)";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
