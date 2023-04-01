package me.alek.command.subcommands;

import me.alek.command.SubCommand;
import org.bukkit.command.CommandSender;

public class SimpleScanCommand implements SubCommand {
    @Override
    public void perform(CommandSender sender, String[] args) {
        ScanPerform.perform(sender, args, false);
    }

    @Override
    public String getUsage() {
        return "/antimalware simplescan <all eller filnavn>";
    }

    @Override
    public String getName() {
        return "simplescan";
    }

    @Override
    public String getDescription() {
        return "Scanner filer for virus og giver en konkret vurdering.";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
