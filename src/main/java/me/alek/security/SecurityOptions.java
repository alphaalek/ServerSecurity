package me.alek.security;

import lombok.Getter;
import me.alek.security.operator.OperatorManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class SecurityOptions {

    @Getter private boolean enabled = false;
    @Getter private boolean preventCancelledMaliciousChatEvents = false;
    @Getter private boolean opProxyBlockerEnabled = false;

    public SecurityOptions(final SecurityConfig securityConfig) {
        final FileConfiguration config = securityConfig.getFileConfiguration();
        this.enabled = config.getBoolean("security-enabled");
        if (enabled) {
            this.preventCancelledMaliciousChatEvents = config.getBoolean("prevent-cancelled-malicious-chat-event");
            this.opProxyBlockerEnabled = config.getBoolean("op-proxy-blocker");
        }
        if (opProxyBlockerEnabled) {
            List<String> opPlayersAllowed = config.getStringList("target");
            if (!opPlayersAllowed.isEmpty()) {
                OperatorManager.get().setAllowedOpPlayers(opPlayersAllowed);
            }
        }
    }

}
