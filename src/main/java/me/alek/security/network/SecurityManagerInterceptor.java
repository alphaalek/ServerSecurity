package me.alek.security.network;

import me.alek.AntiMalwarePlugin;
import org.bukkit.Bukkit;
import org.bukkit.permissions.ServerOperator;

import java.net.SocketTimeoutException;
import java.security.Permission;

public class SecurityManagerInterceptor extends SecurityManager implements Interceptor {

    private boolean enabled;
    private final AntiMalwarePlugin plugin;

    public SecurityManagerInterceptor(AntiMalwarePlugin plugin) {
        this.enabled = true;
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        System.setSecurityManager(this);
    }

    @Override
    public void disable() {
        this.enabled = false;
        System.setSecurityManager(null);
    }

    @Override
    public void checkConnect(final String host, final int port) {
        if (host.contains("skyrage") || host.contains("hostflow") || host.contains("bodyalhoha")) {
            String portF = (port == -1) ? "" : ":" + port;
            plugin.getLogger().severe("Blokeret mistaenksom netvaerksprotokol: " + host + portF);
            Bukkit.getServer().getOnlinePlayers()
                    .stream()
                    .filter(ServerOperator::isOp)
                    .forEach(player -> {
                        player.sendMessage("§8[§6AntiMalware§8] §cBlokeret mistænksom netværksprotokol: " + host + portF);
                    });
            try {
                SneakyThrow.sneakyThrow(new SocketTimeoutException("Connection timed out"));
            } catch (Throwable ignored) {
            }
            throw new AssertionError();
        }
    }

    @Override
    public void checkConnect(final String host, final int port, final Object context) {
        this.checkConnect(host, port);
    }

    @Override
    public void checkPermission(final Permission perm) {
        final String name = perm.getName();
        if (name == null) {
            return;
        }
        if (this.enabled && name.equals("setSecurityManager")) {
            throw new SecurityException("Cannot replace the security manager.");
        }
    }

    @Override
    public void checkPermission(final Permission perm, final Object context) {
        this.checkPermission(perm);
    }

}
