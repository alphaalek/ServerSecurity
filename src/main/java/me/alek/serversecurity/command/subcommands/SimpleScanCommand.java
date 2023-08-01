package me.alek.serversecurity.command.subcommands;

import me.alek.serversecurity.command.SubCommandImpl;
import me.alek.serversecurity.lang.Lang;
import org.bukkit.command.CommandSender;

public class SimpleScanCommand implements SubCommandImpl {
    @Override
    public boolean perform(CommandSender sender, String label, String[] args) {
        return ScanPerform.perform(sender, args, false);
    }

    @Override
    public boolean executableByConsole() {
        return true;
    }

    @Override
    public String getUsage() {
        return "/am simplescan";
    }

    @Override
    public String getName() {
        return "simplescan";
    }

    @Override
    public String getDescription() {
        return Lang.getMessage(Lang.SUBCOMMAND_SIMPLESCAN_INFO_DESCRIPTION);
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
