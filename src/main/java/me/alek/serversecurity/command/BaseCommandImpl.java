package me.alek.serversecurity.command;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.lang.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseCommandImpl implements CommandExecutor, CommandImpl {

    final ArrayList<SubCommandImpl> subCommands = new ArrayList<>();

    public BaseCommandImpl() {
        subCommands.addAll(getSubCommands());
    }

    public void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(Lang.getMessage(Lang.COMMAND_HELP_HEADER) + ((ServerSecurityPlugin.get().getLatestVersion() == null) ? "" : " §c(Outdated)"));
        sender.sendMessage("");
        sender.sendMessage("§6* §8Discord: §7https://discord.gg/KvHahGdFYQ");
        sender.sendMessage("§6* §8Sourcecode: §7https://github.com/alphaalek/ServerSecurity");
        sender.sendMessage("");
        sender.sendMessage(Lang.getMessage(Lang.COMMAND_HELP_FOOTER));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        perform(sender, label, args);
        return true;
    }

    @Override
    public void perform(CommandSender sender, String label, String[] args) {
        if (this instanceof PermissibleCommandImpl) {
            if (!sender.hasPermission(((PermissibleCommandImpl)this).getPermission())) {
                sender.sendMessage(Lang.getMessageWithPrefix(Lang.COMMAND_NO_PERMISSION));
                return;
            }
        }
        if (args.length == 0) {
            performSingle(sender, args);
            return;
        }
        SubCommandImpl subCommandImpl = subCommands.stream().filter(subCommand -> {
            if (subCommand.getName().equalsIgnoreCase(args[0])) return true;
            return Arrays.stream(subCommand.getAliases()).anyMatch(alias -> alias.equalsIgnoreCase(args[0]));
        }).findAny().orElse(null);

        if (subCommandImpl == null) {
            sendHelpMessage(sender);
            return;
        }
        if (subCommandImpl instanceof PermissibleCommandImpl) {
            if (!sender.hasPermission(((PermissibleCommandImpl)subCommandImpl).getPermission())) {
                sender.sendMessage(Lang.getMessageWithPrefix(Lang.COMMAND_NO_PERMISSION));
                return;
            }
        }
        subCommandImpl.perform(sender, label, args);
    }

    public abstract void performSingle(CommandSender sender, String[] args);

    public abstract List<SubCommandImpl> getSubCommands();

}
