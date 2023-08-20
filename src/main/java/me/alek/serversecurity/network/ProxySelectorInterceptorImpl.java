package me.alek.serversecurity.network;


import me.alek.serversecurity.ServerSecurityPlugin;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class ProxySelectorInterceptorImpl implements NetworkInterceptor {

    private final ServerSecurityPlugin plugin;
    private boolean enabled;

    public ProxySelectorInterceptorImpl(ServerSecurityPlugin plugin) {
        this.plugin = plugin;
        this.enabled = true;
    }

    @Override
    public void enable() {
        final ProxySelector selector = ProxySelector.getDefault();

        if (selector instanceof LoggingSelectorWrapper) return;

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

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void checkConnect(String host, int port) {
    }

    private static final class LoggingSelectorWrapper extends ProxySelector {
        private final ProxySelector delegate;

        private LoggingSelectorWrapper(final ProxySelector proxySelector) {
            this.delegate = proxySelector;
        }

        @Override
        public List<Proxy> select(final URI uri) {
            CommonNetworkInterceptor.check(uri.getHost(), uri.getPort());

            return this.delegate.select(uri);
        }

        @Override
        public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
            this.delegate.connectFailed(uri, sa, ioe);
        }
    }
}
