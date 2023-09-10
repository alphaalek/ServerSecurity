package me.alek.serversecurity.command.subcommands;

import me.alek.serversecurity.command.SubCommandImpl;
import me.alek.serversecurity.lang.Lang;
import org.bukkit.command.CommandSender;

public class DeepScanCommand implements SubCommandImpl {

    @Override
    public void perform(CommandSender sender, String label, String[] args) {
        ScanPerform.perform(sender, args, true);
    }

    @Override
    public String getUsage() {
        return "/am deepscan";
    }

    @Override
    public String getName() {
        return "deepscan";
    }

    @Override
    public String getDescription() {
        return Lang.getMessage(Lang.SUBCOMMAND_DEEPSCAN_INFO_DESCRIPTION);
    }

    @Override
    public String[] getAliases() {
        return new String[]{"scan"};
    }
}
