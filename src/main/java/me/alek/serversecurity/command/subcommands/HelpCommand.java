package me.alek.serversecurity.command.subcommands;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.command.BaseCommandImpl;
import me.alek.serversecurity.command.CommandImpl;
import me.alek.serversecurity.command.SubCommandImpl;
import me.alek.serversecurity.lang.Lang;
import org.bukkit.command.CommandSender;

public class HelpCommand implements SubCommandImpl {

    private final BaseCommandImpl command;

    public HelpCommand(BaseCommandImpl command) {
        this.command = command;
    }

    @Override
    public void perform(CommandSender sender, String label, String[] args) {
        sender.sendMessage(Lang.getMessage(Lang.COMMAND_HELP_HEADER) + ((ServerSecurityPlugin.get().getLatestVersion() == null) ? "" : " §c(Outdated)"));
        command.getSubCommands().forEach(subCommand -> sender.sendMessage(" §a" + subCommand.getUsage() + " > §7" + subCommand.getDescription()));
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
