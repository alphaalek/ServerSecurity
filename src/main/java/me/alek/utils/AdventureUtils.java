package me.alek.utils;

import lombok.Getter;
import me.alek.AntiMalwarePlugin;
import me.alek.model.ResultData;
import me.alek.model.result.CheckResult;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class AdventureUtils {

    @Getter
    private final Audience audience;

    public AdventureUtils(Player player) {
        final BukkitAudiences adventure = AntiMalwarePlugin.audience();
        if (adventure == null) {
            this.audience = null;
            return;
        }
        this.audience = adventure.player(player);
    }

    private Component nl(Component component) {
        return component.appendNewline();
    }

    private String fixString(String string) {
        String strip = string.substring(0, string.length());
        ChatColor.stripColor(strip);
        if (strip.length() > 34) {
            return strip.substring(0, 34) + "...";
        }
        return strip;
    }

    private Component append(Component component, String string) {
        return component.append(Component.text(fixString(string)));
    }

    private Component hoverComponent(ResultData data) {
        double level = data.getLevel();
        Component component = Component.text(fixString(ChatUtils.getChatSymbol(level) + "§r" + ChatUtils.getChatColor(level) + data.getFile().getName()));
        component = nl(component);
        component = append(component, "§7" + data.getFile().getPath());
        HashMap<CheckResult, Double> resultMap = new HashMap<>();
        for (CheckResult result : data.getResults()) {
            resultMap.put(result, result.getRisk().getDetectionLevel());
        }
        List<Map.Entry<CheckResult, Double>> pulledResults = new ArrayList<>();
        pulledResults.addAll(resultMap.entrySet());
        if (!pulledResults.isEmpty()) component = nl(component);
        pulledResults.sort(Map.Entry.comparingByValue());
        Collections.reverse(pulledResults);
        int i = 0;
        for (Map.Entry<CheckResult, Double> entry : pulledResults) {
            i++;
            if (i > 5) {
                component = nl(component);
                component = append(component, "  §7... og " + (pulledResults.size() - i + 1) + " mere...");
                break;
            }
            CheckResult result = entry.getKey();
            String variant = result.getVariant();
            if (!variant.equals("")) {
                variant = " (" + variant + ")";
            }
            String className = "";
            String classNameRaw = result.getClassName();
            if (classNameRaw != null) {
                className = classNameRaw;
            }

            component = nl(component);
            component = append(component, "§7- " + result.getRisk().getChatColor() + result.getDetection() + variant);
            if (!className.equals("")) {
                component = nl(component);
                component = append(component, "  §7➟ Class: " + className);
            }
        }
        return component;
    }

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().character('§').build();

    public void send(String str, ResultData data) {
        TextComponent component = serializer.deserialize(str).hoverEvent(HoverEvent.showText(hoverComponent(data)));
        audience.sendMessage(component);
    }
}
