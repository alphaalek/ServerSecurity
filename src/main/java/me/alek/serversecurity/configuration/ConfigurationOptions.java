package me.alek.serversecurity.configuration;

import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.lang.Locale;
import me.alek.serversecurity.network.NetworkHandler;
import me.alek.serversecurity.security.operator.OperatorManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigurationOptions {

    private final Locale locale;
    private final boolean securityEnabled;
    private boolean preventCancelledMaliciousChatEvents;
    private boolean opProxyBlockerEnabled;
    private boolean throwExceptionOnOp;

    public ConfigurationOptions(final Configuration configuration) {
        final FileConfiguration config = configuration.getFileConfiguration();

        // security
        this.securityEnabled = config.getBoolean("security-mode-enabled");
        if (securityEnabled) {
            this.preventCancelledMaliciousChatEvents = config.getBoolean("prevent-cancelled-malicious-chat-event");
            this.opProxyBlockerEnabled = config.getBoolean("op-proxy-blocker");
            this.throwExceptionOnOp = config.getBoolean("throw-exception-on-op");
        }

        // op proxy
        if (opProxyBlockerEnabled) {
            List<String> opPlayersAllowed = config.getStringList("target");
            OperatorManager.get().setAllowedOpPlayers(opPlayersAllowed);
        }

        // language
        this.locale = Locale.fromString(config.getString("locale"));
        Lang.clearMessages();

        // network
        List<String> blockedAddresses = config.getStringList("blocked-target");
        NetworkHandler.get().setBlockedAddresses(blockedAddresses);
    }

    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    public boolean isPreventCancelledMaliciousChatEvents() {
        return preventCancelledMaliciousChatEvents;
    }

    public boolean isOpProxyBlockerEnabled() {
        return opProxyBlockerEnabled;
    }

    public boolean isThrowExceptionOnOp() {
        return throwExceptionOnOp;
    }

    public Locale getLocale() {
        return locale;
    }

}
