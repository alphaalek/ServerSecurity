package me.alek.utils;

import me.alek.AntiMalwarePlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

public class AdventureUtils {

    private BukkitAudiences adventure = AntiMalwarePlugin.audience();
    private final Audience audience;

    public AdventureUtils(Player player) {
        this.audience = adventure.player(player);
    }

    public void send(String str) {
        TextComponent component = Component.text(str);
        audience.sendMessage(component);
    }
}
