package me.alek.serversecurity.utils;

import me.alek.serversecurity.lang.Lang;

public class ScanUtils {

    public static String getChatColor(double level) {
        if (level >= 300) {
            return "§4";
        }
        if (level >= 130) {
            return "§c";
        }
        if (level > 80) {
            return "§e";
        }
        return "§a";
    }

    public static String getChatSymbol(double level) {
        if (level >= 300) {
            return "§4⚠ ";
        }
        if (level >= 130) {
            return "§c⚠ ";
        }
        if (level > 80) {
            return "§e✓ ";
        }
        return "§a✓ ";
    }

    public static String getMessage(double level, boolean deepScan, String fileName) {

        String message;
        if (level >= 300) {
            message = Lang.getMessage(Lang.SCANNING_RESULT_MALWARE);
        }
        else if (level >= 130) {
            message = Lang.getMessage(Lang.SCANNING_RESULT_RED);
        }
        else if (level > 80) {
            message = Lang.getMessage(Lang.SCANNING_RESULT_YELLOW);
        }
        else {
            message = Lang.getMessage(Lang.SCANNING_RESULT_GREEN);
        }
        if (!deepScan)
            message = message.replace(Lang.getMessage(Lang.SCANNING_RESULT_REPLACER), fileName);

        return message;
    }
}
