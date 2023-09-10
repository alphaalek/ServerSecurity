package me.alek.serversecurity.command.commands;

import me.alek.serversecurity.command.BaseCommandImpl;
import me.alek.serversecurity.command.SubCommandImpl;
import me.alek.serversecurity.command.subcommands.*;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class MainCommand extends BaseCommandImpl {

    private final List<SubCommandImpl> subCommand = Arrays.asList(
            new HelpCommand(this),
            new ReloadCommand(),
            new CleanSkyrageCommand(),
            new DeepScanCommand(),
            new SimpleScanCommand()
    );

    @Override
    public void performSingle(CommandSender sender, String[] args) {
        sendHelpMessage(sender);
    }

    @Override
    public List<SubCommandImpl> getSubCommands() {
        return subCommand;
    }
}