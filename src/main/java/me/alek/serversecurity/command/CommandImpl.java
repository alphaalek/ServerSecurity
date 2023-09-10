package me.alek.serversecurity.command;

import org.bukkit.command.CommandSender;

public interface CommandImpl {

    void perform(CommandSender sender, String label, String[] args);

}
