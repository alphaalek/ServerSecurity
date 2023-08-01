package me.alek.serversecurity.command.subcommands;

import me.alek.serversecurity.command.HelpSubCommandImpl;
import me.alek.serversecurity.lang.Lang;
import org.bukkit.command.CommandSender;

public class HelpCommand implements HelpSubCommandImpl {
    @Override
    public boolean perform(CommandSender sender, String label, String[] args) {
        return true;
    }

    @Override
    public boolean executableByConsole() {
        return true;
    }

    @Override
    public String getUsage() {
        return "/am help";
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return Lang.getMessage(Lang.SUBCOMMAND_HELP_INFO_DESCRIPTION);
    }

    @Override
    public String[] getAliases() {
        return new String[]{"hjælp"};
    }
}
