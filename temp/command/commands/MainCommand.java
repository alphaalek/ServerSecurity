package me.alek.command.commands;

import me.alek.command.AbstractCommand;
import me.alek.command.SubCommand;
import me.alek.command.subcommands.DeepScanCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class MainCommand extends AbstractCommand {

    @Override
    public void performSingle(CommandSender sender, String[] args) {
        sendHelpMessage(this, sender);
    }

    @Override
    public boolean executableAsConsole() {
        return false;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public ArrayList<SubCommand> registerSubCommands() {
        ArrayList<SubCommand> subCommands = new ArrayList<>();
        subCommands.add(new DeepScanCommand());
        return subCommands;
    }
}
