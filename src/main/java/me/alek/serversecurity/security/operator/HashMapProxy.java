package me.alek.serversecurity.security.operator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.logging.LogHolder;
import net.minecraft.server.v1_8_R3.JsonListEntry;
import net.minecraft.server.v1_8_R3.OpListEntry;
import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import me.alek.serversecurity.security.SecurityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class HashMapProxy<K, V> extends HashMap<K, V> {

    private final OperatorManager operatorManager;
    private final SecurityManager securityManager;

    public HashMapProxy(SecurityManager securityManager, OperatorManager operatorManager, Map<K, V> ops) {
        this.securityManager = securityManager;
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

                if (name.equals(""))
                    name = "Ukendt";
            }
            if (!operatorManager.isPlayerAllowed(player)) {

                LogHolder.getOPLogger().log(Level.WARN, Lang.getMessageFormatted(Lang.SECURITY_OPPROXY_BLOCKED_LOG, name));

                for (OpListEntry entry : ((CraftServer)Bukkit.getServer()).getHandle().getOPs().getValues()) {
                    OfflinePlayer oppedOfflinePlayer = getPlayer((V)entry);
                    if (!oppedOfflinePlayer.isOnline()) continue;

                    final Player oppedPlayer = (Player) oppedOfflinePlayer;
                    oppedPlayer.sendMessage(Lang.getMessageFormattedWithPrefix(Lang.SECURITY_OPPROXY_BLOCKED_INFO, name));
                }

                remove(key);

                if (ServerSecurityPlugin.get().getConfiguration().getOptions().isThrowExceptionOnOp()) {
                    throw new AssertionError(Lang.getMessageFormatted(Lang.SECURITY_OPPROXY_BLOCKED_INFO, name));
                }
                else {
                    return null;
                }
            }
            operatorManager.put(player, true);

            if (super.get(key) == null) {
                LogHolder.getOPLogger().log(Level.INFO, Lang.getMessageFormatted(Lang.SECURITY_OPPROXY_LOG_OP));
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
            LogHolder.getOPLogger().log(Level.INFO, Lang.getMessageFormatted(Lang.SECURITY_OPPROXY_LOG_DEOP));
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
