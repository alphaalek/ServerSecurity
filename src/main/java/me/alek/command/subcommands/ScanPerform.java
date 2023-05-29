package me.alek.command.subcommands;

import me.alek.*;
import me.alek.scanning.Loader;
import me.alek.scanning.ScanHandler;
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
        final Player player = (Player) sender;
        final Scanner scanner = ScanHandler.getLatestScanner();
        if (scanner.isScanning()) {
            int size = scanner.getFiles().size() - scanner.getService().getNotDoneFiles().size() - 1;
            player.sendMessage("§8[§6AntiMalware§8] §7Serveren er igang med at opdatere cache i auto-update. Vent lidt... ("
                    + (size == -1 ? 0 : size) + "/" + scanner.getFiles().size() + ")");
            return;
        }
        final List<File> files = new ArrayList<>();
        final File dataFolder = AntiMalwarePlugin.getInstance().getDataFolder().getParentFile();
        if (args.length == 1 || args[1].equalsIgnoreCase("all")) {
            final List<File> jars = JARFinder.findAllJars(dataFolder);
            if (jars != null) {
                files.addAll(jars);
            }
        } else {
            final File target = JARFinder.findFile(dataFolder, args[1]);
            files.add(target);
        }

        if (files.isEmpty() || files.stream().filter(Objects::isNull).collect(Collectors.toList()).size() == files.size()) {
            player.sendMessage("§8[§6AntiMalware§8] §cKunne ikke finde noget plugin...");
            return;
        }
        final Loader loader = new Loader(player, deepScan, files);
        loader.load(ScanHandler.getLatestScanner()).sendFeedback();
    }
}
