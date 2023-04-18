package me.alek.security.event;

import me.alek.AntiMalwarePlugin;
import me.alek.security.event.wrappers.WrappedEventController;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

public class EventController<EVENT extends Event> implements Listener {

    private final List<EVENT> cancelOnCallbackEvents = new ArrayList<>();

    public void addCancelOnCallback(EVENT event) {
        cancelOnCallbackEvents.add(event);
    }

    public EventController() {
        Bukkit.getPluginManager().registerEvents(this, AntiMalwarePlugin.getInstance());
    }

    @EventHandler
    public void onControlEvent(WrappedEventController event) {
        if (event.getType() == ControllerType.THREAD_START) {
            cancelOnCallbackEvents.clear();
        } else if (event.getType() == ControllerType.CALLBACK) {
            for (EVENT cancellableEvent : cancelOnCallbackEvents) {
                if (cancellableEvent instanceof Cancellable) {
                    ((Cancellable)cancellableEvent).setCancelled(true);
                }
            }
            CancellationEventProxy.getHandlerListContainer().clearId(event.getId());
            ChatExecutorBlocker.getAlreadyNotifiedEvent().removeNotifiedId(event.getId());
        }
    }

    @EventHandler
    public void block(BlockBreakEvent event) {

    }
}
