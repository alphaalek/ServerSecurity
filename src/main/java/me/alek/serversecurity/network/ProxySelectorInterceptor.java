package me.alek.serversecurity.network;


import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.logging.LogHolder;
import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.permissions.ServerOperator;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class ProxySelectorInterceptor implements Interceptor {

    private final ServerSecurityPlugin plugin;
    private boolean enabled;

    public ProxySelectorInterceptor(ServerSecurityPlugin plugin) {
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

    private static final class LoggingSelectorWrapper extends ProxySelector {
        private final ProxySelector delegate;

        private LoggingSelectorWrapper(final ProxySelector proxySelector) {
            this.delegate = proxySelector;
        }

        @Override
        public List<Proxy> select(final URI uri) {
            String authority = uri.getAuthority();
            if (authority.contains("skyrage") || authority.contains("hostflow") || authority.contains("bodyalhoha")) {

                LogHolder.getSecurityLogger().log(Level.WARN, Lang.getMessageFormatted(Lang.NETWORK_BLOCKED, authority));

                Bukkit.getServer().getOnlinePlayers()
                        .stream()
                        .filter(ServerOperator::isOp)
                        .forEach(player -> player.sendMessage(Lang.getMessageFormattedWithPrefix(Lang.NETWORK_BLOCKED, authority)));
                try {
                    SneakyThrow.sneakyThrow(new SocketTimeoutException("Connection timed out"));
                } catch (Throwable ignored) {
                }

                throw new AssertionError(Lang.getMessageFormatted(Lang.NETWORK_BLOCKED, authority));
            }
            return this.delegate.select(uri);
        }

        @Override
        public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
            this.delegate.connectFailed(uri, sa, ioe);
        }
    }
}
