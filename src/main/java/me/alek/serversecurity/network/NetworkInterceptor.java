package me.alek.serversecurity.network;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.lang.Lang;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Level;

public class NetworkInterceptor {

    private final Map<InterceptMethod, Interceptor> interceptors;
    private final ServerSecurityPlugin plugin;

    public NetworkInterceptor(ServerSecurityPlugin plugin) {
        this.plugin = plugin;
        this.interceptors = new EnumMap<>(InterceptMethod.class);
    }

    public void enable() {
        this.setupInterceptors();
        for (final Interceptor interceptor : this.interceptors.values()) {
            try {
                interceptor.enable();
            } catch (final Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, Lang.getMessage(Lang.NETWORK_ERROR_ACTIVATING_INTERCEPTOR));
            }
        }
    }

    public void disable() {
        for (final Interceptor interceptor : this.interceptors.values()) {
            interceptor.disable();
        }
    }

    private void setupInterceptors() {
        for (final InterceptMethod method : InterceptMethod.values()) {
            try {
                final Constructor<? extends Interceptor> constructor = method.clazz.getDeclaredConstructor(ServerSecurityPlugin.class);
                final Interceptor interceptor = constructor.newInstance(this.plugin);
                this.interceptors.put(method, interceptor);
            } catch (final Throwable t) {
                this.plugin.getLogger().log(Level.SEVERE, Lang.getMessage(Lang.NETWORK_ERROR_ACTIVATING_INTERCEPTOR));
            }
        }
    }

    private enum InterceptMethod {
        SECURITY_MANAGER(SecurityManagerInterceptor.class),
        PROXY_SELECTOR(ProxySelectorInterceptor.class);

        private final Class<? extends Interceptor> clazz;

        InterceptMethod(final Class<? extends Interceptor> clazz) {
            this.clazz = clazz;
        }
    }
}
