package me.alek.security;

import lombok.Getter;
import me.alek.security.config.SecurityConfig;
import org.bukkit.configuration.file.FileConfiguration;
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
