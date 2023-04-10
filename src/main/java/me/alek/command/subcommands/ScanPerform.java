package me.alek.command.subcommands;

import me.alek.*;
import me.alek.scanning.Loader;
import me.alek.scanning.ScanManager;
import me.alek.scanning.Scanner;
import me.alek.utils.JARFinder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScanPerform {

    public static void perform(CommandSender sender, String[] args, boolean deepScan) {
        Player player = (Player) sender;
        Scanner scanner = ScanManager.getLatestScanner();
        if (scanner.isScanning()) {
            int size = scanner.getFiles().size();
            player.sendMessage("§8[§6AntiMalware§8] §7Serveren er igang med at opdatere cache i auto-update. Vent lidt... ("
                    + (size - scanner.getService().getNotDoneFiles().size() - 1) + "/" + size + ")");
            return;
        }
        List<File> files = new ArrayList<>();
        File dataFolder = AntiMalwarePlugin.getInstance().getDataFolder().getParentFile();
        if (args.length == 1 || args[1].equalsIgnoreCase("all")) {
            List<File> jars = JARFinder.findAllJars(dataFolder);
            if (jars != null) {
                files.addAll(jars);
            }
        } else {
            File target = JARFinder.findFile(dataFolder, args[1]);
            files.add(target);
        }

        if (files.isEmpty() || files.stream().filter(Objects::isNull).collect(Collectors.toList()).size() == files.size()) {
            player.sendMessage("§8[§6AntiMalware§8] §cKunne ikke finde noget plugin...");
            return;
        }
        Loader loader = new Loader(player, deepScan, files);
        loader.load(ScanManager.getLatestScanner()).sendFeedback();
    }
}
