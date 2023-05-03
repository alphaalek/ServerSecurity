package me.alek.security.blocker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.alek.AntiMalwarePlugin;
import me.alek.security.SecurityManager;
import me.alek.security.SecurityOptions;
import me.alek.security.blocker.listeners.ChatListener2;
import me.alek.security.blocker.listeners.CommandPreprocessListener;
import me.alek.security.blocker.listeners.PlayerKickListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class ListenerRegistery {

    private final SecurityManager securityManager;

    public ListenerRegistery(SecurityManager manager, PluginManager pluginManager) {
        this.securityManager = manager;
        for (Class<? extends AbstractListener> listenerClass : getListeners()) {
            try {
                final Constructor<? extends AbstractListener> constructor = listenerClass.getDeclaredConstructor(SecurityManager.class, PluginManager.class);
                final AbstractListener listener = constructor.newInstance(manager, pluginManager);
                Bukkit.getPluginManager().registerEvents(listener, AntiMalwarePlugin.getInstance());
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Class<? extends AbstractListener>> getListeners() {
        List<Class<? extends AbstractListener>> listeners = new ArrayList<>(Arrays.asList(
                CommandPreprocessListener.class,
                PlayerKickListener.class,
                ChatListener2.class,
                CommandInjector.class));
        SecurityOptions securityOptions = securityManager.getOptions();
        if (securityOptions.isPreventCancelledMaliciousChatEvents()) {
            listeners.add(ExecutorDetector.class);
        }
        return listeners;
    }
}
