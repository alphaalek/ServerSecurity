package me.alek.serversecurity.lang;

import me.alek.serversecurity.ServerSecurityPlugin;

import java.util.*;

public enum Lang {

    COMMAND_NO_PERMISSION,
    COMMAND_NO_CONSOLE,
    COMMAND_HELP_HEADER,

    SUBCOMMAND_CLEAN_INFO_DESCRIPTION,
    SUBCOMMAND_CLEAN_SPECIFICATION,
    SUBCOMMAND_CLEAN_CLEANING,
    SUBCOMMAND_CLEAN_OS_NOT_INFECTED,
    SUBCOMMAND_CLEAN_PLUGINS_NOT_INFECTED,
    SUBCOMMAND_CLEAN_PLUGINS_CLEANING,
    SUBCOMMAND_CLEAN_PLUGINS_TOTAL_CLEANED,

    SUBCOMMAND_LANGUAGE_INFO_DESCRIPTION,
    SUBCOMMAND_LANGUAGE_ERROR_NO_SUCH_LANGUAGE,
    SUBCOMMAND_LANGUAGE_ERROR_BAD_CONFIG,
    SUBCOMMAND_LANGUAGE_ERROR_SPECIFICATION,
    SUBCOMMAND_LANGUAGE_CHANGED,

    SUBCOMMAND_DEEPSCAN_INFO_DESCRIPTION,
    SUBCOMMAND_SIMPLESCAN_INFO_DESCRIPTION,
    SUBCOMMAND_HELP_INFO_DESCRIPTION,
    SUBCOMMAND_RELOAD_INFO_DESCRIPTION,
    SUBCOMMAND_RELOAD_RELOADING,

    SCANNING_ERROR_NO_AVAILABLE_SCANNER,
    SCANNING_ERROR_NO_SUCH_PLUGIN,
    SCANNING_ERROR_ALREADY_SCANNING,
    SCANNING_ERROR_NO_RESULT,
    SCANNING_ERROR_UNKNOWN,
    SCANNING_WARN_MANY_INFECTED,
    SCANNING_STARTING,
    SCANNING_DONE_CHAT,
    SCANNING_DONE_LOG,
    SCANNING_WARN_INFECTED_JOIN,
    SCANNING_RESULT_GREEN,
    SCANNING_RESULT_YELLOW,
    SCANNING_RESULT_RED,
    SCANNING_RESULT_MALWARE,
    SCANNING_RESULT_REPLACER,

    CLEANING_ERROR_PROCESS_RESERVED,
    CLEANING_WINDOWS_ERROR_NO_WRITE_PERMISSION,
    CLEANING_WINDOWS_ERROR_TASK_KILL,
    CLEANING_LINUX_ERROR_ABORTED,
    CLEANING_LINUX_ERROR_NO_WRITE_PERMISSION,

    NETWORK_ERROR_ACTIVATING_INTERCEPTOR,
    NETWORK_ERROR_REPLACE_SECURITY_MANAGER,
    NETWORK_BLOCKED,

    SECURITY_BLOCKED_COMMAND,
    SECURITY_BLOCKED_OPERATOR_CHANGE,
    SECURITY_BLOCKED_BACKDOOR_CHAT,
    SECURITY_BLOCKED_BACKDOOR_KICK,
    SECURITY_BLOCKED_BACKDOOR_RCE_LOG,
    SECURITY_BLOCKED_BACKDOOR_RCE_OPERATOR,
    SECURITY_OPPROXY_BLOCKED_LOG,
    SECURITY_OPPROXY_BLOCKED_INFO,
    SECURITY_OPPROXY_LOG_OP,
    SECURITY_OPPROXY_LOG_DEOP,

    UPDATE_FOUND

    ;

    private static final Map<Lang, String[]> messageMap = new HashMap<>();

    public static void initializeMessages(Locale locale) {
        for (LocaleMessageRepresentor representorValue : locale.getLangs()) {
            messageMap.put(representorValue.getLang(), representorValue.getMessages());
        }
    }

    public static void clearMessages() {
        messageMap.clear();
    }

    public static String[] getMessages(Lang lang) {
        if (messageMap.isEmpty())
            initializeMessages(ServerSecurityPlugin.get().getConfiguration().getOptions().getLocale());

        if (!messageMap.containsKey(lang))
            return new String[]{"§c\"Failed to load message\""};

        return messageMap.get(lang);
    }

    public static String getMessage(Lang lang) {
        return getMessages(lang)[0];
    }

    public static String getMessageFormatted(Lang lang, Object... format) {
        return String.format(getMessage(lang), format);
    }

    public static String getMessageWithPrefix(Lang lang) {
        return "§6ServerSecurity §8\u00BB " + getMessage(lang);
    }

    public static String getMessageFormattedWithPrefix(Lang lang, Object... format) {
        return String.format(getMessageWithPrefix(lang), format);
    }

}
