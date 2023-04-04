package me.alek.command.subcommands;

import me.alek.command.HelpSubCommand;
import me.alek.command.SubCommand;
import org.bukkit.command.CommandSender;

public class HelpCommand implements HelpSubCommand {
    @Override
    public void perform(CommandSender sender, String[] args) {

    }

    @Override
    public String getUsage() {
        return "/antimalware help";
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Sender kommandoer du kan bruge";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"hjælp"};
    }
}
