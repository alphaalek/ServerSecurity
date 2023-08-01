package me.alek.serversecurity.command.subcommands;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.malware.scanning.Loader;
import me.alek.serversecurity.malware.scanning.Scanner;
import me.alek.serversecurity.utils.JARFinder;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScanPerform {

    public static boolean perform(CommandSender sender, String[] args, boolean deepScan) {
        final Scanner scanner = Scanner.latestScanner;

        if (scanner == null) {
            sender.sendMessage(Lang.getMessageWithPrefix(Lang.SCANNING_ERROR_NO_AVAILABLE_SCANNER));
            return true;
        }

        if (scanner.isScanning()) {
            int size = scanner.getDone();
            sender.sendMessage(Lang.getMessageFormattedWithPrefix(Lang.SCANNING_ERROR_ALREADY_SCANNING, (size == -1 ? 0 : size), scanner.getSize()));
            return true;
        }

        final List<File> files = new ArrayList<>();
        final File dataFolder = ServerSecurityPlugin.get().getDataFolder().getParentFile();

        if (args.length == 1 || args[1].equalsIgnoreCase("all")) {
            final List<File> jars = JARFinder.findAllJars(dataFolder);
            if (jars != null) {
                files.addAll(jars);
            }

        } else {
            final File target = JARFinder.findJar(dataFolder, args[1]);
            files.add(target);
        }

        if (files.isEmpty() || files.stream().filter(Objects::isNull).count() == files.size()) {
            sender.sendMessage(Lang.getMessageWithPrefix(Lang.SCANNING_ERROR_NO_SUCH_PLUGIN));
            return true;
        }

        final Loader loader = new Loader(deepScan, files, scanner);
        loader.sendFeedback(sender);

        return true;
    }
}
