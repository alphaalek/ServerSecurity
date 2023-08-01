package me.alek.serversecurity.command;

import org.bukkit.command.CommandSender;

public interface CommandImpl {

    boolean perform(CommandSender sender, String label, String[] args);
}
