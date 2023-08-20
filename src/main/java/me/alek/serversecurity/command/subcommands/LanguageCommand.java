package me.alek.serversecurity.command.subcommands;

import me.alek.serversecurity.command.SubCommandImpl;
import me.alek.serversecurity.configuration.Configuration;
import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.lang.Locale;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class LanguageCommand implements SubCommandImpl {
    @Override
    public boolean perform(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(Lang.getMessageWithPrefix(Lang.SUBCOMMAND_LANGUAGE_ERROR_SPECIFICATION));
            return true;
        }
        if (!Locale.hasLocale(args[1])) {
            sender.sendMessage(Lang.getMessageWithPrefix(Lang.SUBCOMMAND_LANGUAGE_ERROR_NO_SUCH_LANGUAGE));
            return true;
        }

        Configuration configuration = ServerSecurityPlugin.get().getConfiguration();
        FileConfiguration fileConfiguration = configuration.getFileConfiguration();

        if (fileConfiguration.get("locale") == null) {
            sender.sendMessage(Lang.getMessageWithPrefix(Lang.SUBCOMMAND_LANGUAGE_ERROR_BAD_CONFIG));
            return true;
        }

        fileConfiguration.set("locale", args[1]);
        configuration.reload();

        sender.sendMessage(Lang.getMessageWithPrefix(Lang.SUBCOMMAND_LANGUAGE_CHANGED));
        return true;
    }

    @Override
    public boolean executableByConsole() {
        return true;
    }

    @Override
    public String getUsage() {
        return "/am language";
    }

    @Override
    public String getName() {
        return "language";
    }

    @Override
    public String getDescription() {
        return Lang.getMessage(Lang.SUBCOMMAND_LANGUAGE_INFO_DESCRIPTION);
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
