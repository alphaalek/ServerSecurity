package me.alek.serversecurity.command.commands;

import me.alek.serversecurity.command.BaseCommandImpl;
import me.alek.serversecurity.command.PermissibleCommandImpl;
import me.alek.serversecurity.command.SubCommandImpl;
import me.alek.serversecurity.command.subcommands.*;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class MainCommand extends BaseCommandImpl implements PermissibleCommandImpl {

    @Override
    public void performSingle(CommandSender sender, String[] args) {
        sendHelpMessage(sender);
    }


    @Override
    public String getPermission() {
        return "antimalware.use";
    }

    @Override
    public List<SubCommandImpl> registerSubCommands() {
        return Arrays.asList(
                new HelpCommand(),
                new LanguageCommand(),
                new ReloadCommand(),
                new CleanSkyrageCommand(),
                new DeepScanCommand(),
                new SimpleScanCommand()
        );
    }
}