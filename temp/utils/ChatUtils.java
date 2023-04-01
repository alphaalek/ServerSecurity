package me.alek.utils;

public class ChatUtils {

    public static String getChatColor(double level) {
        if (level >= 30) {
            return "§4";
        }
        if (level >= 10) {
            return "§c";
        }
        if (level > 5) {
            return "§e";
        }
        return "§a";
    }

    public static String getChatSymbol(double level) {
        if (level >= 30) {
            return "§4!?! ";
        }
        if (level >= 10) {
            return "§c⚠ ";
        }
        if (level > 5) {
            return "§e✓ ";
        }
        return "§a✓ ";
    }

    public static String getMessage(double level) {
        if (level >= 30) {
            return "§4Dette plugin har virus! Fjern det omgående!";
        }
        if (level >= 10) {
            return "§cDette plugin har måske virus!";
        }
        if (level > 5) {
            return "§eDette plugin har højst sandsynligt ikke virus.";
        }
        return "§aDette plugin har ikke virus.";
    }
}
