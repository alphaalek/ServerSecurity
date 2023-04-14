package me.alek.security.network;

import me.alek.AntiMalwarePlugin;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Level;

public class NetworkInterceptor {

    private final Map<InterceptMethod, Interceptor> interceptors;
    private final AntiMalwarePlugin plugin;

    public NetworkInterceptor(AntiMalwarePlugin plugin) {
        this.plugin = plugin;
        this.interceptors = new EnumMap<>(InterceptMethod.class);
    }

    public void enable() {
        this.setupInterceptors();
        for (final Interceptor interceptor : this.interceptors.values()) {
            try {
                interceptor.enable();
            } catch (final Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, "Exception occurred whilst enabling " + interceptor.getClass().getName(), e);
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
                final Constructor<? extends Interceptor> constructor = method.clazz.getDeclaredConstructor(AntiMalwarePlugin.class);
                final Interceptor interceptor = constructor.newInstance(this.plugin);
                this.interceptors.put(method, interceptor);
            } catch (final Throwable t) {
                this.plugin.getLogger().log(Level.SEVERE, "Exception occurred whilst initialising method " + method, t);
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
