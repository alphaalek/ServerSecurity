package me.alek.serversecurity.security.blocker.wrappers;

import me.alek.serversecurity.security.blocker.EventController;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

public class WrappedEventController extends Event {

    private final RegisteredListener listener;
    private final long id;
    private final EventController.ControllerType type;
    private static HandlerList handlers = new HandlerList();

    public WrappedEventController(RegisteredListener listener, long id, EventController.ControllerType type) {
        this.listener = listener;
        this.id = id;
        this.type = type;
    }

    public EventController.ControllerType getType() {
        return type;
    }

    public RegisteredListener getListener() {
        return listener;
    }

    public long getId() {
        return id;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
