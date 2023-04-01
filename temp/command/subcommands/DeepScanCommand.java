package me.alek.command.subcommands;

import me.alek.AntiMalwarePlugin;
import me.alek.JARLoader;
import me.alek.Scanner;
import me.alek.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class DeepScanCommand implements SubCommand {

    @Override
    public void perform(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        List<File> files = JARLoader.findAllJars(AntiMalwarePlugin.getInstance().getDataFolder().getParentFile());

        if (files.isEmpty()) {
            player.sendMessage("§cKunne ikke scanne plugins...");
            return;
        }

        new Scanner(files, player).scan();
    }

    @Override
    public String getUsage() {
        return "/antimalware deepscan";
    }

    @Override
    public String getName() {
        return "deepscan";
    }

    @Override
    public String getDescription() {
        return "Scanner dine plugins igennem.";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
