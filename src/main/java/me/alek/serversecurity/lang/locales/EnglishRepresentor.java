package me.alek.serversecurity.lang.locales;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.lang.LocaleMessageRepresentor;

public enum EnglishRepresentor implements LocaleMessageRepresentor {

    COMMAND_NO_PERMISSION(Lang.COMMAND_NO_PERMISSION, "You do not have permission to use this command!"),
    COMMAND_NO_CONSOLE(Lang.COMMAND_NO_CONSOLE, "§cThis command can only be used by players."),
    COMMAND_HELP_HEADER(Lang.COMMAND_HELP_HEADER,
            "§6" + ServerSecurityPlugin.get().getDescription().getFullName() + ". Made by Alek05."),

    SUBCOMMAND_CLEAN_INFO_DESCRIPTION(Lang.SUBCOMMAND_CLEAN_INFO_DESCRIPTION, "Cleans OS or plugins from Skyrage."),
    SUBCOMMAND_CLEAN_SPECIFICATION(Lang.SUBCOMMAND_CLEAN_SPECIFICATION, "§cYou need to specify, what you would like to clean. Use /am cleanskyrage <system/plugins>."),
    SUBCOMMAND_CLEAN_CLEANING(Lang.SUBCOMMAND_CLEAN_CLEANING, "§7Cleaning the OS of the server from Skyrage Malware..."),
    SUBCOMMAND_CLEAN_OS_NOT_INFECTED(Lang.SUBCOMMAND_CLEAN_OS_NOT_INFECTED, "§aIt looks like the OS of the server is not infected by Skyrage Malware."),
    SUBCOMMAND_CLEAN_PLUGINS_NOT_INFECTED(Lang.SUBCOMMAND_CLEAN_PLUGINS_NOT_INFECTED, "§aIt looks like there isn't any plugin infected by Skyrage Malware."),
    SUBCOMMAND_CLEAN_PLUGINS_CLEANING(Lang.SUBCOMMAND_CLEAN_PLUGINS_CLEANING, "§aRemoving Skyrage Malware from file %s..."),
    SUBCOMMAND_CLEAN_PLUGINS_TOTAL_CLEANED(Lang.SUBCOMMAND_CLEAN_PLUGINS_TOTAL_CLEANED, "§aRemoved Skyrage Malware from %s files."),

    SUBCOMMAND_LANGUAGE_INFO_DESCRIPTION(Lang.SUBCOMMAND_LANGUAGE_INFO_DESCRIPTION, "Change the language of the plugin."),
    SUBCOMMAND_LANGUAGE_ERROR_NO_SUCH_LANGUAGE(Lang.SUBCOMMAND_LANGUAGE_ERROR_NO_SUCH_LANGUAGE, "§cNo language was found by that string."),
    SUBCOMMAND_LANGUAGE_ERROR_BAD_CONFIG(Lang.SUBCOMMAND_LANGUAGE_ERROR_BAD_CONFIG, "§cYou may have a old config or typed something badly in it. Remove it, restart the server and try again."),
    SUBCOMMAND_LANGUAGE_ERROR_SPECIFICATION(Lang.SUBCOMMAND_LANGUAGE_ERROR_SPECIFICATION, "§cYou have to chose a language."),
    SUBCOMMAND_LANGUAGE_CHANGED(Lang.SUBCOMMAND_LANGUAGE_CHANGED, "§aThe language is now changed to english."),

    SUBCOMMAND_DEEPSCAN_INFO_DESCRIPTION(Lang.SUBCOMMAND_DEEPSCAN_INFO_DESCRIPTION, "Detailed scan of files with virus and backdoors."),
    SUBCOMMAND_SIMPLESCAN_INFO_DESCRIPTION(Lang.SUBCOMMAND_SIMPLESCAN_INFO_DESCRIPTION, "Simple scan of files with virus and backdoors."),
    SUBCOMMAND_HELP_INFO_DESCRIPTION(Lang.SUBCOMMAND_HELP_INFO_DESCRIPTION, "Sends all the commands you can use."),
    SUBCOMMAND_RELOAD_INFO_DESCRIPTION(Lang.SUBCOMMAND_RELOAD_INFO_DESCRIPTION, "Reloads file cache and configuration."),
    SUBCOMMAND_RELOAD_RELOADING(Lang.SUBCOMMAND_RELOAD_RELOADING, "§7Reloading file cache and configuration..."),

    SCANNING_ERROR_NO_AVAILABLE_SCANNER(Lang.SCANNING_ERROR_NO_AVAILABLE_SCANNER, "§cThere encountered an error. Try waiting some time."),
    SCANNING_ERROR_NO_SUCH_PLUGIN(Lang.SCANNING_ERROR_NO_SUCH_PLUGIN, "§cCouldn't find any plugin that match."),
    SCANNING_ERROR_ALREADY_SCANNING(Lang.SCANNING_ERROR_ALREADY_SCANNING, "§cThe server is already caching scannings. Wait some time... (%s/%s)"),
    SCANNING_ERROR_NO_RESULT(Lang.SCANNING_ERROR_NO_RESULT, "§cThere wasn't found any data from the scan."),
    SCANNING_ERROR_UNKNOWN(Lang.SCANNING_ERROR_UNKNOWN, "§cThere encountered an error. Try waiting some time."),
    SCANNING_WARN_MANY_INFECTED(Lang.SCANNING_WARN_MANY_INFECTED, "§cIt looks like, that the server has a lot of files infected. " +
            "This could be due to one plugin infecting all of the other ones. You would have to install all of them again, and " +
            "ServerSecurity as well."),
    SCANNING_STARTING(Lang.SCANNING_STARTING, "§7Scanning %s files for malware and backdoors..."),
    SCANNING_DONE_CHAT(Lang.SCANNING_DONE_CHAT, "§7Done scanning %s files and found %s with malware or backdoor."),
    SCANNING_DONE_LOG(Lang.SCANNING_DONE_LOG, "Done scanning %s files and found %s with malware or backdoor."),
    SCANNING_WARN_INFECTED_JOIN(Lang.SCANNING_WARN_INFECTED_JOIN, "§cThere has been found malware in a plugin. Use /am scan for more information."),
    SCANNING_RESULT_GREEN(Lang.SCANNING_RESULT_GREEN, "§aThis plugin is not infected."),
    SCANNING_RESULT_YELLOW(Lang.SCANNING_RESULT_YELLOW, "§eThis plugin is most likely not infected."),
    SCANNING_RESULT_RED(Lang.SCANNING_RESULT_RED, "§cThis plugin is might infected."),
    SCANNING_RESULT_MALWARE(Lang.SCANNING_RESULT_MALWARE, "§4This plugin is infected! Remove it immediately!"),
    SCANNING_RESULT_REPLACER(Lang.SCANNING_RESULT_REPLACER, "This plugin"),

    CLEANING_ERROR_PROCESS_RESERVED(Lang.CLEANING_ERROR_PROCESS_RESERVED, "§cThe file is reserved by a different proces and can therefore not be modified."),
    CLEANING_WINDOWS_ERROR_NO_WRITE_PERMISSION(Lang.CLEANING_WINDOWS_ERROR_NO_WRITE_PERMISSION, "§cThe server is still infected! You have to give access to modify files."),
    CLEANING_WINDOWS_ERROR_TASK_KILL(Lang.CLEANING_WINDOWS_ERROR_TASK_KILL, "§cThe server is still infected! There encountered an error when trying to kill the process."),
    CLEANING_LINUX_ERROR_ABORTED(Lang.CLEANING_LINUX_ERROR_ABORTED, "§cThe server is still infected! The cleaning was aborted by some other thread or process."),
    CLEANING_LINUX_ERROR_NO_WRITE_PERMISSION(Lang.CLEANING_LINUX_ERROR_NO_WRITE_PERMISSION, "§cThe server is still infected! You have to give access to modify files."),

    NETWORK_ERROR_ACTIVATING_INTERCEPTOR(Lang.NETWORK_ERROR_ACTIVATING_INTERCEPTOR, "§cThere encountered an error when activating the network interceptor."),
    NETWORK_ERROR_REPLACE_SECURITY_MANAGER(Lang.NETWORK_ERROR_REPLACE_SECURITY_MANAGER, "§cThere encountered an error when deactivating the network interceptor."),
    NETWORK_BLOCKED(Lang.NETWORK_BLOCKED, "Blocked protocol from address: %s"),

    SECURITY_BLOCKED_COMMAND(Lang.SECURITY_BLOCKED_COMMAND, "Blocked console command: %s"),
    SECURITY_BLOCKED_OPERATOR_CHANGE(Lang.SECURITY_BLOCKED_OPERATOR_CHANGE, "Blocked operator shift: %s -> %s (%s)"),
    SECURITY_BLOCKED_BACKDOOR_CHAT(Lang.SECURITY_BLOCKED_BACKDOOR_CHAT, "§cPossible backdoor was found! Used by: %s"),
    SECURITY_BLOCKED_BACKDOOR_KICK(Lang.SECURITY_BLOCKED_BACKDOOR_KICK, "§cPossible backdoor was found! Contact a member of the staff team, if this is a mistake."),
    SECURITY_OPPROXY_BLOCKED_LOG(Lang.SECURITY_OPPROXY_BLOCKED_LOG, "Operator of player was denied: %s"),
    SECURITY_OPPROXY_BLOCKED_INFO(Lang.SECURITY_OPPROXY_BLOCKED_INFO, "§cOperator of player was denied: %s. If the player " +
            "should be an operator, you can give the player access in config.yml"),
    SECURITY_OPPROXY_LOG_OP(Lang.SECURITY_OPPROXY_LOG_OP, "Player is now op: %s"),
    SECURITY_OPPROXY_LOG_DEOP(Lang.SECURITY_OPPROXY_LOG_DEOP, "Player is now deop: %s"),

    UPDATE_FOUND(Lang.UPDATE_FOUND, "§7Found an update: v%s. Download:")
    ;

    private final Lang lang;
    private final String[] messages;

    EnglishRepresentor(Lang lang, String... messages) {
        this.lang = lang;
        this.messages = messages;
    }

    @Override
    public Lang getLang() {
        return lang;
    }

    @Override
    public String[] getMessages() {
        return messages;
    }
}
