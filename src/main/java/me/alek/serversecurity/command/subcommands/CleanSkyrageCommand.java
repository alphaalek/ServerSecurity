package me.alek.serversecurity.command.subcommands;

import me.alek.serversecurity.command.SubCommandImpl;
import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.malware.cleaning.OperatingSystem;
import me.alek.serversecurity.malware.cleaning.SkyrageJarCleaner;
import me.alek.serversecurity.malware.cleaning.SystemCleaner;
import me.alek.serversecurity.malware.cleaning.SystemInfectionType;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CleanSkyrageCommand implements SubCommandImpl {
    @Override
    public boolean perform(CommandSender sender, String label, String[] args) {

        boolean sendHelpMessage = false;
        if (args.length == 1) {
            sendHelpMessage = true;
        } else if (!(args[1].equals("system") || args[1].equals("plugins"))) {
            sendHelpMessage = true;
        }
        if (sendHelpMessage) {
            sender.sendMessage(Lang.getMessageWithPrefix(Lang.SUBCOMMAND_CLEAN_SPECIFICATION));
            return true;
        }

        if (args[1].equals("system")) {
            sender.sendMessage(Lang.getMessageWithPrefix(Lang.SUBCOMMAND_CLEAN_CLEANING));
            OperatingSystem system = OperatingSystem.getSystem();
            SystemCleaner cleaner = system.getCleaner();
            try {
                SystemInfectionType type = cleaner.getInfection();
                if (type != null) {
                    cleaner.clean(type, sender);
                } else {
                    sender.sendMessage(Lang.getMessageWithPrefix(Lang.SUBCOMMAND_CLEAN_OS_NOT_INFECTED));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                List<File> infectedJars = SkyrageJarCleaner.findInfectedJars(new File("plugins").getAbsoluteFile().getParentFile());
                if (infectedJars.size() == 0) {
                    sender.sendMessage(Lang.getMessageWithPrefix(Lang.SUBCOMMAND_CLEAN_PLUGINS_NOT_INFECTED));
                    return true;
                }
                boolean success = false;
                for (File jar : infectedJars) {
                    sender.sendMessage(Lang.getMessageFormatted(Lang.SUBCOMMAND_CLEAN_PLUGINS_CLEANING, jar.getName()));
                    success = SkyrageJarCleaner.cleanJar(sender, jar);
                }
                if (success) {
                    sender.sendMessage(Lang.getMessageFormattedWithPrefix(Lang.SUBCOMMAND_CLEAN_PLUGINS_TOTAL_CLEANED, infectedJars.size()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean executableByConsole() {
        return true;
    }

    @Override
    public String getUsage() {
        return "/am cleanskyrage";
    }

    @Override
    public String getName() {
        return "cleanskyrage";
    }

    @Override
    public String getDescription() {
        return Lang.getMessage(Lang.SUBCOMMAND_CLEAN_INFO_DESCRIPTION);
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
