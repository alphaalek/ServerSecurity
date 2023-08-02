package me.alek.serversecurity.network;

import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.logging.LogHolder;
import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.permissions.ServerOperator;

import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

public class CommonNetworkInterceptor {

    private final static List<String> defaultBlockedAddresses = Arrays.asList("skyrage", "bodyalhoha", "hostflow");

    public static void check(String host, int port) {
        boolean shouldBlock = false;

        for (String address : NetworkHandler.get().getBlockedAddresses()) {

            if (host.equals(address)) {
                shouldBlock = true;
                break;
            }
        }
        if (!shouldBlock) {
            for (String address : defaultBlockedAddresses) {

                if (host.contains(address)) {
                    shouldBlock = true;
                    break;
                }
            }
        }
        if (!shouldBlock) return;

        String format = (port <= 0) ? host : host + ":" + port;

        LogHolder.getSecurityLogger().log(Level.WARN, Lang.getMessageFormatted(Lang.NETWORK_BLOCKED, format));

        Bukkit.getServer().getOnlinePlayers()
                .stream()
                .filter(ServerOperator::isOp)
                .forEach(player -> player.sendMessage(Lang.getMessageFormattedWithPrefix(Lang.NETWORK_BLOCKED, format)));
        try {
            SneakyThrow.sneakyThrow(new SocketTimeoutException("Connection timed out"));
        } catch (Throwable ignored) {
        }

        throw new AssertionError(Lang.getMessageFormatted(Lang.NETWORK_BLOCKED, format));
    }
}
