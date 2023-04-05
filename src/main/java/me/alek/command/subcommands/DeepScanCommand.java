package me.alek.command.subcommands;

import me.alek.command.SubCommand;
import org.bukkit.command.CommandSender;

public class DeepScanCommand implements SubCommand {

    @Override
    public void perform(CommandSender sender, String[] args) {
        ScanPerform.perform(sender, args, true);
    }

    @Override
    public String getUsage() {
        return "/antimalware deepscan <all eller pluginnavn>";
    }

    @Override
    public String getName() {
        return "deepscan";
    }

    @Override
    public String getDescription() {
        return "Scanner filer for virus og giver en detaljeret rapport.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"scan"};
    }
}
