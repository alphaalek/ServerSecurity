package me.alek.utils;

public class ChatUtils {

    public static String getChatColor(double level) {
        if (level >= 30) {
            return "§4";
        }
        if (level > 8) {
            return "§c";
        }
        if (level > 5) {
            return "§e";
        }
        return "§a";
    }

    public static String getChatSymbol(double level) {
        if (level >= 30) {
            return "§4⚠ ";
        }
        if (level > 8) {
            return "§c⚠ ";
        }
        if (level > 5) {
            return "§e✓ ";
        }
        return "§a✓ ";
    }

    private static String fixMessage(String str, boolean deepScan, String fileName) {
        if (deepScan) {
            return str;
        }
        return str.replaceAll("Dette plugin", fileName);
    }

    public static String getMessage(double level, boolean deepScan, String fileName) {
        if (level >= 30) {
            return fixMessage("§4Dette plugin har virus! Fjern det omgående!", deepScan, fileName);
        }
        if (level > 8) {
            return fixMessage("§cDette plugin har måske virus!", deepScan, fileName);
        }
        if (level > 5) {
            return fixMessage("§eDette plugin har højst sandsynligt ikke virus.", deepScan, fileName);
        }
        return fixMessage("§aDette plugin har ikke virus.", deepScan, fileName);
    }
}
