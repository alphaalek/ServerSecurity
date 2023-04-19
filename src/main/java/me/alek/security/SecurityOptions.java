package me.alek.security;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

public class SecurityOptions {

    @Getter private boolean enabled = false;
    @Getter private boolean preventCancelledMaliciousChatEvents = false;

    public SecurityOptions(final SecurityConfig securityConfig) {
        YamlConfiguration config = securityConfig.getYamlConfiguration();
        this.enabled = config.getBoolean("security-enabled");
        if (enabled) {
            this.preventCancelledMaliciousChatEvents = config.getBoolean("prevent-cancelled-malicious-chat-event");
        }
    }

}
