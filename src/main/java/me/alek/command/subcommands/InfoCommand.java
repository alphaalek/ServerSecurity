package me.alek.command.subcommands;

import me.alek.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand implements SubCommand {

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.sendMessage("§8[§6AntiMalware§8] §7AntiMalware scanner for følgende ting:");
        player.sendMessage("§4⚠ Malware: §7Hostflow, SkyRage, Qlutch, Ectasy, OpenEctasy, ThiccIndustries, Bukloit, OpenBukloit");
        player.sendMessage("§c⚠ Høj risiko: §7Obfuscated, Force OP, Bytecode Manipulation");
        player.sendMessage("§e✓ Moderat risiko: §7Dispatch Command, Discord Webhook, L10 Class, System Access, User-Agent Request, Embedded JAR, Websocket");
        player.sendMessage("§a✓ Lav risiko: §7Cipher Encryption, Base64, ClassLoader, Hidden File, IP Grabber, Load Plugins, OpenConnection, System Property, AsyncPlayerChatEvent");
        player.sendMessage("§8[§6AntiMalware§8] §7Bare fordi et plugin bliver flagget for nogen af de ovenstående ting, betyder det ikke at det har virus. " +
                "Dog er det typiske tegn på, at et plugin har en integreret backdoor eller andre former for ting der ikke burde være der.");
    }

    @Override
    public String getUsage() {
        return "/antimalware info";
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Sender information omkring AntiMalware pluginnet.";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
