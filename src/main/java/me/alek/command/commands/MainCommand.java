package me.alek.command.commands;

import me.alek.command.AbstractCommand;
import me.alek.command.SubCommand;
import me.alek.command.subcommands.DeepScanCommand;
import me.alek.command.subcommands.SimpleScanCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public List<SubCommand> registerSubCommands() {
        return Arrays.asList(
                new DeepScanCommand(),
                new SimpleScanCommand()
        );
    }
}
