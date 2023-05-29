package me.alek.security.operator;

import com.mojang.authlib.GameProfile;
import lombok.SneakyThrows;
import me.alek.logging.LogHolder;
import me.alek.network.SneakyThrow;
import net.minecraft.server.v1_8_R3.JsonListEntry;
import net.minecraft.server.v1_8_R3.OpListEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HashMapProxy<K, V> extends HashMap<K, V> {

    private final OperatorManager operatorManager;

    public HashMapProxy(OperatorManager operatorManager, Map<K, V> ops) {
        this.operatorManager = operatorManager;
        for (Map.Entry<K, V> entry : ops.entrySet()) {
            super.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V put(K key, V value) {
        OfflinePlayer player = getPlayer(value);
        if (player != null) {
            if (!operatorManager.isPlayerAllowed(player)) {
                LogHolder.getOPLogger().log(Level.SEVERE, "OP spiller blev blokeret: " + player.getName() + " (" + player.getUniqueId() + ")");
                for (OpListEntry entry : ((CraftServer)Bukkit.getServer()).getHandle().getOPs().getValues()) {
                    OfflinePlayer oppedOfflinePlayer = getPlayer((V)entry);
                    if (!oppedOfflinePlayer.isOnline()) continue;

                    Player oppedPlayer = (Player) oppedOfflinePlayer;
                    oppedPlayer.sendMessage("§8[§6AntiMalware§8] §cOpped spiller blev blokeret: " + player.getName() + ". " +
                            "Hvis spilleren skal oppes, kan du give spilleren tilladelse i config.yml.");
                }
                return null;
            }

            operatorManager.put(player, true);

            if (super.get(key) == null) {
                LogHolder.getOPLogger().log(Level.INFO, "Spiller blev op: " + player.getName() + " (" + player.getUniqueId() + ")");
            }
        }

        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {
        V value = super.get(key);
        if (value == null) {
            return null;
        }
        OfflinePlayer player = getPlayer(value);
        if (player != null) {
            operatorManager.put(player, false);
            LogHolder.getOPLogger().log(Level.INFO, "Spiller blev deop: " + player.getName() + " (" + player.getUniqueId() + ")");
        }

        return super.remove(key);
    }

    private OfflinePlayer getPlayer(V value) {
        return Bukkit.getOfflinePlayer(((GameProfile) ((JsonListEntry) value).getKey()).getId());
    }
}
