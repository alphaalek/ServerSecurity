package me.alek.serversecurity.network;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.lang.Lang;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Level;

public class NetworkHandler {

    private final Map<InterceptMethod, NetworkInterceptor> interceptors;
    private final List<String> blockedAddresses = new ArrayList<>();
    private final ServerSecurityPlugin plugin;

    private static NetworkHandler instance;

    public NetworkHandler(ServerSecurityPlugin plugin) {
        synchronized (this) {
            if (instance != null) {
                setBlockedAddresses(instance.getBlockedAddresses());
            }
        }
        this.interceptors = new EnumMap<>(InterceptMethod.class);
        this.plugin = plugin;
        instance = this;
    }

    public static synchronized NetworkHandler get() {
        if (instance == null) {
            instance = new NetworkHandler(ServerSecurityPlugin.get());
        }
        return instance;
    }

    public void enable() {
        this.setupInterceptors();
        for (final NetworkInterceptor interceptor : this.interceptors.values()) {
            try {
                interceptor.enable();
            } catch (final Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, Lang.getMessage(Lang.NETWORK_ERROR_ACTIVATING_INTERCEPTOR));
            }
        }
    }

    public void disable() {
        for (final NetworkInterceptor interceptor : this.interceptors.values()) {
            interceptor.disable();
        }
    }

    public void setBlockedAddresses(List<String> addresses) {
        this.blockedAddresses.addAll(addresses);
    }

    public List<String> getBlockedAddresses() {
        return this.blockedAddresses;
    }

    private void setupInterceptors() {
        for (final InterceptMethod method : InterceptMethod.values()) {
            try {
                final Constructor<? extends NetworkInterceptor> constructor = method.clazz.getDeclaredConstructor(ServerSecurityPlugin.class);
                final NetworkInterceptor interceptor = constructor.newInstance(this.plugin);
                this.interceptors.put(method, interceptor);
            } catch (final Throwable t) {
                this.plugin.getLogger().log(Level.SEVERE, Lang.getMessage(Lang.NETWORK_ERROR_ACTIVATING_INTERCEPTOR));
            }
        }
    }

    public Collection<NetworkInterceptor> getInterceptors() {
        return this.interceptors.values();
    }

    public List<NetworkInterceptor> getEnabledInterceptors() {
        List<NetworkInterceptor> enabledInterceptors = new ArrayList<>();

        for (NetworkInterceptor interceptor : this.interceptors.values()) {

            if (interceptor.isEnabled()) {
                enabledInterceptors.add(interceptor);
            }
        }
        return enabledInterceptors;
    }

    public List<NetworkInterceptor> getDisabledInterceptors() {
        List<NetworkInterceptor> disabledInterceptors = new ArrayList<>();

        for (NetworkInterceptor interceptor : this.interceptors.values()) {

            if (!interceptor.isEnabled()) {
                disabledInterceptors.add(interceptor);
            }
        }
        return disabledInterceptors;
    }

    private enum InterceptMethod {
        SECURITY_MANAGER(SecurityManagerInterceptorImpl.class),
        PROXY_SELECTOR(ProxySelectorInterceptorImpl.class);

        private final Class<? extends NetworkInterceptor> clazz;

        InterceptMethod(final Class<? extends NetworkInterceptor> clazz) {
            this.clazz = clazz;
        }
    }
}
