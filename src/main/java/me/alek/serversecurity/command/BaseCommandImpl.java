package me.alek.serversecurity.command;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.lang.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseCommandImpl implements CommandExecutor, CommandImpl {

    final ArrayList<SubCommandImpl> subCommands = new ArrayList<>();

    public void init() {
        subCommands.addAll(registerSubCommands());
    }

    public BaseCommandImpl() {
        init();
    }

    public void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(Lang.getMessage(Lang.COMMAND_HELP_HEADER) + ((ServerSecurityPlugin.get().getLatestVersion() == null) ? "" : " §c(Outdated)"));
        subCommands.forEach(subCommand -> sender.sendMessage(" §a" + subCommand.getUsage() + " > §7" + subCommand.getDescription()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return perform(sender, label, args);
    }

    @Override
    public boolean perform(CommandSender sender, String label, String[] args) {
        if (this instanceof PermissibleCommandImpl) {
            if (!sender.hasPermission(((PermissibleCommandImpl)this).getPermission())) {
                sender.sendMessage(Lang.getMessageWithPrefix(Lang.COMMAND_NO_PERMISSION));
                return true;
            }
        }
        if (args.length == 0) {
            performSingle(sender, args);
            return true;
        }
        SubCommandImpl subCommandImpl = subCommands.stream().filter(subCommand -> {
            if (subCommand.getName().equalsIgnoreCase(args[0])) return true;
            return Arrays.stream(subCommand.getAliases()).anyMatch(alias -> alias.equalsIgnoreCase(args[0]));
        }).findAny().orElse(null);

        if (subCommandImpl == null || subCommandImpl instanceof HelpSubCommandImpl) {
            sendHelpMessage(sender);
            return true;
        }
        if (!subCommandImpl.executableByConsole()) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Lang.getMessageWithPrefix(Lang.COMMAND_NO_CONSOLE));
                return true;
            }
        }
        if (subCommandImpl instanceof PermissibleCommandImpl) {
            if (!sender.hasPermission(((PermissibleCommandImpl)subCommandImpl).getPermission())) {
                sender.sendMessage(Lang.getMessageWithPrefix(Lang.COMMAND_NO_PERMISSION));
                return true;
            }
        }
        subCommandImpl.perform(sender, label, args);
        return true;
    }

    public abstract void performSingle(CommandSender sender, String[] args);

    public abstract List<SubCommandImpl> registerSubCommands();

}
