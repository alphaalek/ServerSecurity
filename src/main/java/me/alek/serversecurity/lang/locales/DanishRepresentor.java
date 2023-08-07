package me.alek.serversecurity.lang.locales;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.lang.LocaleMessageRepresentor;

public enum DanishRepresentor implements LocaleMessageRepresentor {

    COMMAND_NO_PERMISSION(Lang.COMMAND_NO_PERMISSION, "§cDu har ikke adgang til denne kommando."),
    COMMAND_NO_CONSOLE(Lang.COMMAND_NO_CONSOLE, "§cDenne kommando kan kun blive skrevet af spillere."),
    COMMAND_HELP_HEADER(Lang.COMMAND_HELP_HEADER,
                "§6" + ServerSecurityPlugin.get().getDescription().getFullName() + ". Lavet af Alek05."),

    SUBCOMMAND_CLEAN_INFO_DESCRIPTION(Lang.SUBCOMMAND_CLEAN_INFO_DESCRIPTION, "Renser OS eller plugins fra Skyrage."),
    SUBCOMMAND_CLEAN_SPECIFICATION(Lang.SUBCOMMAND_CLEAN_SPECIFICATION, "§cDu mangler at specificere, hvad du vil rense. Brug /am cleanskyrage <system/plugins>."),
    SUBCOMMAND_CLEAN_CLEANING(Lang.SUBCOMMAND_CLEAN_CLEANING, "§7Renser serverens OS fra Skyrage Malware..."),
    SUBCOMMAND_CLEAN_OS_NOT_INFECTED(Lang.SUBCOMMAND_CLEAN_OS_NOT_INFECTED, "§aDet ser ud til, at serverens OS ikke er smittet med Skyrage Malware."),
    SUBCOMMAND_CLEAN_PLUGINS_NOT_INFECTED(Lang.SUBCOMMAND_CLEAN_PLUGINS_NOT_INFECTED, "§aDet ser ud til, at serveren ingen plugins har, som er smittet med Skyrage Malware."),
    SUBCOMMAND_CLEAN_PLUGINS_CLEANING(Lang.SUBCOMMAND_CLEAN_PLUGINS_CLEANING, "§aFjerner Skyrage Malware fra fil %s..."),
    SUBCOMMAND_CLEAN_PLUGINS_TOTAL_CLEANED(Lang.SUBCOMMAND_CLEAN_PLUGINS_TOTAL_CLEANED, "§aFjernet Skyrage Malware fra %s filer."),

    SUBCOMMAND_LANGUAGE_INFO_DESCRIPTION(Lang.SUBCOMMAND_LANGUAGE_INFO_DESCRIPTION, "Ændre sproget på pluginnet."),
    SUBCOMMAND_LANGUAGE_ERROR_NO_SUCH_PLUGIN(Lang.SUBCOMMAND_LANGUAGE_ERROR_NO_SUCH_LANGUAGE, "§cDer blev ikke fundet noget plugin ud fra den tekst."),
    SUBCOMMAND_LANGUAGE_ERROR_BAD_CONFIG(Lang.SUBCOMMAND_LANGUAGE_ERROR_BAD_CONFIG, "§cDu har enten en gammel config eller skrevet noget vrøvl i den. Slet den, genstart serveren og prøv igen."),
    SUBCOMMAND_LANGUAGE_ERROR_SPECIFICATION(Lang.SUBCOMMAND_LANGUAGE_ERROR_SPECIFICATION, "§cDu skal vælge et sprog."),
    SUBCOMMAND_LANGUAGE_CHANGED(Lang.SUBCOMMAND_LANGUAGE_CHANGED, "§aSproget er nu ændret til dansk."),

    SUBCOMMAND_DEEPSCAN_INFO_DESCRIPTION(Lang.SUBCOMMAND_DEEPSCAN_INFO_DESCRIPTION,"Detaljeret scan af filer for virus og backdoors."),
    SUBCOMMAND_SIMPLESCAN_INFO_DESCRIPTION(Lang.SUBCOMMAND_SIMPLESCAN_INFO_DESCRIPTION, "Simpel scan af filer for virus og backdoors."),
    SUBCOMMAND_HELP_INFO_DESCRIPTION(Lang.SUBCOMMAND_HELP_INFO_DESCRIPTION, "Sender kommandoer du kan bruge."),
    SUBCOMMAND_RELOAD_INFO_DESCRIPTION(Lang.SUBCOMMAND_RELOAD_INFO_DESCRIPTION, "Reloader file cache og konfigurationsfilen."),
    SUBCOMMAND_RELOAD_RELOADING(Lang.SUBCOMMAND_RELOAD_RELOADING, "§7Reloader file cache og konfiruationsfilen..."),

    SCANNING_ERROR_NO_AVAILABLE_SCANNER(Lang.SCANNING_ERROR_NO_AVAILABLE_SCANNER, "§cDer skete en fejl, prøv igen senere."),
    SCANNING_ERROR_NO_SUCH_PLUGIN(Lang.SCANNING_ERROR_NO_SUCH_PLUGIN, "§cKunne ikke finde noget plugin der matcher."),
    SCANNING_ERROR_ALREADY_SCANNING(Lang.SCANNING_ERROR_ALREADY_SCANNING, "§cServeren er i gang med at cache scanninger. Vent lidt... (%s/%s)"),
    SCANNING_ERROR_NO_RESULT(Lang.SCANNING_ERROR_NO_RESULT, "§cDer blev ikke fundet noget data fra scanningen."),
    SCANNING_ERROR_UNKNOWN(Lang.SCANNING_ERROR_UNKNOWN, "§cDer skete en fejl, prøv igen senere."),
    SCANNING_WARN_MANY_INFECTED(Lang.SCANNING_WARN_MANY_INFECTED, "§cDet ser ud til, at serveren har rigtig mange filer smittet. " +
            "Dette kan være fordi, at ét plugin har smittet til alle de andre. Du bør installere alle dine plugins igen, også ServerSecurity."),
    SCANNING_STARTING(Lang.SCANNING_STARTING, "§7Scanner %s filer for malware og backdoors..."),
    SCANNING_DONE_CHAT(Lang.SCANNING_DONE_CHAT, "§7Scannede %s filer i gennem og fandt %s med malware eller backdoor."),
    SCANNING_DONE_LOG(Lang.SCANNING_DONE_LOG, "Scannede %s filer i gennem og fandt %s med malware eller backdoor."),
    SCANNING_WARN_INFECTED_JOIN(Lang.SCANNING_WARN_INFECTED_JOIN, "§cDer er fundet virus i et plugin! Se nærmere med /am scan."),
    SCANNING_RESULT_GREEN(Lang.SCANNING_RESULT_GREEN, "§aDette plugin har ikke virus."),
    SCANNING_RESULT_YELLOW(Lang.SCANNING_RESULT_YELLOW, "§eDette plugin har højst sandsynligt ikke virus."),
    SCANNING_RESULT_RED(Lang.SCANNING_RESULT_RED, "§cDette plugin har måske virus."),
    SCANNING_RESULT_MALWARE(Lang.SCANNING_RESULT_MALWARE, "§4Dette plugin har virus! Fjern det omgående!"),
    SCANNING_RESULT_REPLACER(Lang.SCANNING_RESULT_REPLACER, "Dette plugin"),

    CLEANING_ERROR_PROCESS_RESERVED(Lang.CLEANING_ERROR_PROCESS_RESERVED, "§cFilen bliver optaget af en anden proces og kan derfor ikke tilgås."),
    CLEANING_WINDOWS_ERROR_NO_WRITE_PERMISSION(Lang.CLEANING_WINDOWS_ERROR_NO_WRITE_PERMISSION, "§cServeren er stadig smittet! Sørg for at give adgang til at ændre i filer."),
    CLEANING_WINDOWS_ERROR_TASK_KILL(Lang.CLEANING_WINDOWS_ERROR_TASK_KILL, "§cServeren er stadig smittet! Der opstod en fejl ved dræb af proces."),
    CLEANING_LINUX_ERROR_ABORTED(Lang.CLEANING_LINUX_ERROR_ABORTED, "§cServeren er stadig smittet! Rensningen blev afbrudt af en anden thread eller proces."),
    CLEANING_LINUX_ERROR_NO_WRITE_PERMISSION(Lang.CLEANING_LINUX_ERROR_NO_WRITE_PERMISSION, "§cServeren er stadig smittet! Sørg for at give adgang til at ændre i filer."),

    NETWORK_ERROR_ACTIVATING_INTERCEPTOR(Lang.NETWORK_ERROR_ACTIVATING_INTERCEPTOR, "§cDer skete en fejl ved aktivering af netværk spion."),
    NETWORK_ERROR_REPLACE_SECURITY_MANAGER(Lang.NETWORK_ERROR_REPLACE_SECURITY_MANAGER, "§cDer skete en fejl ved deaktivering af netværk spion."),
    NETWORK_BLOCKED_LOG(Lang.NETWORK_BLOCKED, "Blokeret protokol fra adresse: %s"),

    SECURITY_BLOCKED_COMMAND(Lang.SECURITY_BLOCKED_COMMAND, "Blokeret konsolkommando: %s"),
    SECURITY_BLOCKED_OPERATOR_CHANGE(Lang.SECURITY_BLOCKED_OPERATOR_CHANGE, "Blokeret operator skift: %s -> %s (%s)"),
    SECURITY_BLOCKED_BACKDOOR_CHAT(Lang.SECURITY_BLOCKED_BACKDOOR_CHAT, "§cMulig backdoor blev opfanget! Udnyttet af: %s"),
    SECURITY_BLOCKED_BACKDOOR_KICK(Lang.SECURITY_BLOCKED_BACKDOOR_KICK, "§cMulig backdoor blev opfanget! Kontakt en staff, hvis du mener dette er en fejl."),
    SECURITY_OPPROXY_BLOCKED_LOG(Lang.SECURITY_OPPROXY_BLOCKED_LOG, "Operator af spiller blev afslået: %s"),
    SECURITY_OPPROXY_BLOCKED_INFO(Lang.SECURITY_OPPROXY_BLOCKED_INFO, "§cOperator af spiller blev afslået: %s. Hvis spilleren " +
            "skal være operator, kan du give spilleren tilladelse i config.yml"),
    SECURITY_OPPROXY_LOG_OP(Lang.SECURITY_OPPROXY_LOG_OP, "Spiller blev op: %s"),
    SECURITY_OPPROXY_LOG_DEOP(Lang.SECURITY_OPPROXY_LOG_DEOP, "Spiller blev deop: %s"),

    UPDATE_FOUND(Lang.UPDATE_FOUND, "§7Fandt en opdatering: v%s. Download:")
    ;

    private final Lang lang;
    private final String[] messages;

    DanishRepresentor(Lang lang, String... messages) {
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
