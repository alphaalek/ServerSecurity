package me.alek.serversecurity.security.blocker;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.security.blocker.wrappers.WrappedEventController;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventController<EVENT extends Event> implements Listener {

    private final HashMap<String, List<EVENT>> cancelOnCallbackEvents = new HashMap<>();

    public void addCancelOnCallback(String id, EVENT event) {
        cancelOnCallbackEvents.get(id).add(event);
    }

    public EventController() {
        Bukkit.getPluginManager().registerEvents(this, ServerSecurityPlugin.get());
    }

    public enum ControllerType {
        THREAD_START, CALLBACK
    }

    @EventHandler
    public void onControlEvent(WrappedEventController event) {
        if (event.getType() == ControllerType.THREAD_START) {
            cancelOnCallbackEvents.put(String.valueOf(event.getId()), new ArrayList<>());
        } else if (event.getType() == ControllerType.CALLBACK) {
            for (EVENT cancellableEvent : cancelOnCallbackEvents.get(String.valueOf(event.getId()))) {
                if (cancellableEvent instanceof Cancellable) {
                    ((Cancellable)cancellableEvent).setCancelled(true);
                }
            }
            CancellationProxy.getHandlerListContainer().clearId(event.getId());
            ExecutorDetector.alreadyNotifiedEvent.removeNotifiedId(event.getId());
            cancelOnCallbackEvents.remove(String.valueOf(event.getId()));
        }
    }
}
