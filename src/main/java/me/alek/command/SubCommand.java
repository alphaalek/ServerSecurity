package me.alek.command;

import org.bukkit.command.CommandSender;

import java.io.IOException;

public interface SubCommand {

    void perform(CommandSender sender, String[] args);

    String getUsage();

    String getName();

    String getDescription();

    String[] getAliases();
}
