package me.alek.serversecurity.security.blocker;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.security.SecurityManager;
import me.alek.serversecurity.configuration.ConfigurationOptions;
import me.alek.serversecurity.security.injector.CommandInjector;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListenerRegistery {

    private final SecurityManager securityManager;

    public ListenerRegistery(SecurityManager manager, PluginManager pluginManager) {
        this.securityManager = manager;
        for (Class<? extends AbstractListener> listenerClass : getListeners()) {
            try {
                final Constructor<? extends AbstractListener> constructor = listenerClass.getDeclaredConstructor(SecurityManager.class, PluginManager.class);
                final AbstractListener listener = constructor.newInstance(manager, pluginManager);
                Bukkit.getPluginManager().registerEvents(listener, ServerSecurityPlugin.get());
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Class<? extends AbstractListener>> getListeners() {
        List<Class<? extends AbstractListener>> listeners = new ArrayList<>();
        ConfigurationOptions securityOptions = ServerSecurityPlugin.get().getConfiguration().getOptions();
        if (securityOptions.isPreventCancelledMaliciousChatEvents()) {
            listeners.addAll(Arrays.asList(
                    ExecutorDetector.class,
                    CommandInjector.class));
        }
        return listeners;
    }
}
