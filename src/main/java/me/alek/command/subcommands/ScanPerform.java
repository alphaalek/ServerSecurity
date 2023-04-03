package me.alek.command.subcommands;

import me.alek.AntiMalwarePlugin;
import me.alek.JARLoader;
import me.alek.Scanner;
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
        List<File> files = new ArrayList<>();
        File dataFolder = AntiMalwarePlugin.getInstance().getDataFolder().getParentFile();
        if (args.length == 1 || args[1].equalsIgnoreCase("all")) {
            files.addAll(JARLoader.findAllJars(dataFolder));
        } else {
            File target = JARLoader.findFile(dataFolder, args[1]);
            files.add(target);
        }

        if (files.isEmpty() || files.stream().filter(Objects::isNull).collect(Collectors.toList()).size() == files.size()) {
            player.sendMessage("&8[&6AntiMalware&8] §cKunne ikke finde noget plugin...");
            return;
        }

        new Scanner(player, files, deepScan).scan();
    }
}
