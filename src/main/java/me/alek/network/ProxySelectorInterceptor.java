package me.alek.network;


import me.alek.AntiMalwarePlugin;
import me.alek.logging.LogHolder;
import org.bukkit.Bukkit;
import org.bukkit.permissions.ServerOperator;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.logging.Level;

public class ProxySelectorInterceptor implements Interceptor {

    private final AntiMalwarePlugin plugin;
    private boolean enabled;

    public ProxySelectorInterceptor(AntiMalwarePlugin plugin) {
        this.plugin = plugin;
        this.enabled = true;
    }

    @Override
    public void enable() {
        final ProxySelector selector = ProxySelector.getDefault();
        if (selector instanceof LoggingSelectorWrapper) {
            return;
        }
        ProxySelector.setDefault(new LoggingSelectorWrapper(selector));
    }

    @Override
    public void disable() {
        enabled = false;
        final ProxySelector selector = ProxySelector.getDefault();
        if (selector instanceof LoggingSelectorWrapper) {
            final LoggingSelectorWrapper logSelector = (LoggingSelectorWrapper) selector;
            ProxySelector.setDefault(logSelector.delegate);
        }
    }

    private final class LoggingSelectorWrapper extends ProxySelector {
        private final ProxySelector delegate;

        private LoggingSelectorWrapper(final ProxySelector delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<Proxy> select(final URI uri) {
            String authority = uri.getAuthority();
            if (authority.contains("skyrage") || authority.contains("hostflow") || authority.contains("bodyalhoha")) {
                LogHolder.getSecurityLogger().log(Level.SEVERE, "Network protocol fra virus blokeret (websocket/fil download/netty connection): " + authority);
                Bukkit.getServer().getOnlinePlayers()
                        .stream()
                        .filter(ServerOperator::isOp)
                        .forEach(player -> {
                            player.sendMessage("§8[§6AntiMalware§8] §cBlokeret network protocol: " + authority);
                        });
                try {
                    SneakyThrow.sneakyThrow(new SocketTimeoutException("Connection timed out"));
                } catch (Throwable ignored) {
                }
                throw new AssertionError("Virus blokeret! Tjek security.log");
            }
            return this.delegate.select(uri);
        }

        @Override
        public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
            this.delegate.connectFailed(uri, sa, ioe);
        }
    }
}
