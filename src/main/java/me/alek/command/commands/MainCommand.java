package me.alek.command.commands;

import me.alek.command.AbstractCommand;
import me.alek.command.SubCommand;
import me.alek.command.subcommands.*;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class MainCommand extends AbstractCommand {

    @Override
    public void performSingle(CommandSender sender, String[] args) {
        sendHelpMessage(sender);
    }

    @Override
    public boolean executableAsConsole() {
        return false;
    }

    @Override
    public String getPermission() {
        return "antimalware.use";
    }

    @Override
    public List<SubCommand> registerSubCommands() {
        return Arrays.asList(
                new HelpCommand(),
                new CleanSkyrageCommand(),
                new ReloadCommand(),
                new InfoCommand(),
                new DeepScanCommand(),
                new SimpleScanCommand()
        );
    }
}