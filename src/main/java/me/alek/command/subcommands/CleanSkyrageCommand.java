package me.alek.command.subcommands;

import me.alek.cleaning.OperatingSystem;
import me.alek.cleaning.SkyrageJarCleaner;
import me.alek.cleaning.SystemCleaner;
import me.alek.cleaning.SystemInfectionType;
import me.alek.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CleanSkyrageCommand implements SubCommand {
    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        boolean sendHelpMessage = false;
        if (args.length == 1) {
            sendHelpMessage = true;
        } else if (!(args[1].equals("system") || args[1].equals("plugins"))) {
            sendHelpMessage = true;
        }
        if (sendHelpMessage) {
            player.sendMessage("§8[§6AntiMalware§8] §cDu mangler at specificere, hvilken slags rensning du vil gøre brug af. Brug /am cleanskyrage <system/plugins>");
            return;
        }

        if (args[1].equals("system")) {
            player.sendMessage("§8[§6AntiMalware§8] §7Renser dit OS system for botnet fra Skyrage Malware...");
            OperatingSystem system = OperatingSystem.getSystem();
            SystemCleaner cleaner = system.getCleaner();
            try {
                SystemInfectionType type = cleaner.getInfection();
                if (type != null) {
                    cleaner.clean(type, player);
                } else {
                    player.sendMessage("§8[§6AntiMalware§8] §aDet ser ud til, at dit system ikke er smittet med Skyrage Malware.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                List<File> infectedJars = SkyrageJarCleaner.findInfectedJars(new File("plugins").getAbsoluteFile().getParentFile());
                if (infectedJars.size() == 0) {
                    player.sendMessage("§8[§6AntiMalware§8] §aDet ser ud til, at du ingen plugins har som er smittet med Skyrage Malware.");
                    /*if (ScanManager.getLatestScanner().getResultData().stream()
                            .map(ResultData::getResults)
                            .anyMatch(result -> {
                                if (result instanceof MalwareCheckResult) {
                                    MalwareCheckResult malwareResult = (MalwareCheckResult) result;
                                    Bukkit.broadcastMessage(malwareResult.getType().getName());
                                    return malwareResult.getType() == MalwareType.SKYRAGE;
                                }
                                return false;
                            })) {*/
                    player.sendMessage("§8[§6AntiMalware§8] §7Hvis /am deepscan all har skrevet, at et plugin har Skyrage men ikke bliver vist her," +
                            " kan det være fordi, at den ikke har en plugin-config.bin. Fjern pluginnet alligevel, inden Skyrage Malware udvikler sig" +
                            " i pluginnet, og laver et botnet i dit OS system.");

                    return;
                }
                boolean success = false;
                for (File jar : infectedJars) {
                    player.sendMessage("§aFjerner Skyrage Malware fra " + jar.getName() + "... ");
                    success = SkyrageJarCleaner.cleanJar(player, jar);
                }
                if (success) {
                    player.sendMessage("§8[§6AntiMalware§8] §aFjernet Skyrage Malware fra " + infectedJars.size() + " plugins.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getUsage() {
        return "/antimalware cleanskyrage <system/plugins>";
    }

    @Override
    public String getName() {
        return "cleanskyrage";
    }

    @Override
    public String getDescription() {
        return "Renser dit OS system for botnet eller pluginfiler for malware der stammer fra Skyrage. §cDenne kommando er i beta!";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
