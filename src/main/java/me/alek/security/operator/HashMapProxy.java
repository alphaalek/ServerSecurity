package me.alek.security.operator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
        final OfflinePlayer player = getPlayer(value);
        if (player != null) {

            String name = player.getName();
            if (name == null) {
                name = getMojangName(player.getUniqueId().toString());
                if (name.equals("")) {
                    name = "Ukendt";
                }
            }
            if (!operatorManager.isPlayerAllowed(player)) {

                LogHolder.getOPLogger().log(Level.SEVERE, "OP spiller blev blokeret: " + name + " (" + player.getUniqueId() + ")");
                for (OpListEntry entry : ((CraftServer)Bukkit.getServer()).getHandle().getOPs().getValues()) {
                    OfflinePlayer oppedOfflinePlayer = getPlayer((V)entry);
                    if (!oppedOfflinePlayer.isOnline()) continue;

                    final Player oppedPlayer = (Player) oppedOfflinePlayer;
                    oppedPlayer.sendMessage("§8[§6AntiMalware§8] §cOp spiller blev blokeret: " + name + ". " +
                            "Hvis spilleren skal oppes, kan du give spilleren tilladelse i config.yml.");
                }
                return null;
            }

            operatorManager.put(player, true);

            if (super.get(key) == null) {
                LogHolder.getOPLogger().log(Level.INFO, "Spiller blev op: " + name + " (" + player.getUniqueId() + ")");
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

    private String getMojangName(String uuid) {
        final String SESSION_SERVER_URL = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
        try {
            final URL url = new URL(SESSION_SERVER_URL);
            final URLConnection connection = url.openConnection();

            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            final StringBuilder lines = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.append(line);
            }

            final JsonParser parser = new JsonParser();
            final JsonObject json = parser.parse(lines.toString()).getAsJsonObject();

            reader.close();
            return json.get("name").getAsString();
        } catch (IOException ex) {
            return "";
        }
    }
}
