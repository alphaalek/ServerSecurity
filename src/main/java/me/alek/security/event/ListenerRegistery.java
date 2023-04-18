package me.alek.security.event;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.alek.AntiMalwarePlugin;
import me.alek.security.SecurityManager;
import me.alek.security.SecurityOptions;
import me.alek.security.event.listeners.ChatListener2;
import me.alek.security.event.listeners.CommandPreprocessListener;
import me.alek.security.event.listeners.PlayerKickEvent;
import org.bukkit.Bukkit;

public class ListenerRegistery {

    private final SecurityManager securityManager;
    private final List<Class<? extends AbstractListener>> listeners;

    public ListenerRegistery(SecurityManager manager) {
        this.securityManager = manager;
        this.listeners = new CopyOnWriteArrayList<>(getListeners());
        registerListeners();
    }

    private void registerListeners() {
        for (Class<? extends AbstractListener> listenerClass : listeners) {
            try {
                final Constructor<? extends AbstractListener> constructor = listenerClass.getDeclaredConstructor(SecurityManager.class);
                final AbstractListener listener = constructor.newInstance(securityManager);
                Bukkit.getServer().getPluginManager().registerEvents(listener, AntiMalwarePlugin.getInstance());
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized List<Class<? extends AbstractListener>> getListeners() {
        List<Class<? extends AbstractListener>> listeners = new ArrayList<>(Arrays.asList(
                CommandPreprocessListener.class,
                PlayerKickEvent.class,
                ChatListener2.class,
                ChatExecutorBlocker.class));
        /*synchronized (securityManager) {
            SecurityOptions securityOptions = securityManager.getOptions();
            if (securityOptions.isPreventCancelledMaliciousChatEvents()) {
                listeners.add(ChatExecutorBlocker.class);
            }
        }*/
        return listeners;
    }
}
