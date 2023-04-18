package me.alek.security.event.wrappers;

import me.alek.security.event.ControllerType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

public class WrappedEventController extends Event {

    private final RegisteredListener listener;
    private final long id;
    private final ControllerType type;
    private static HandlerList handlers = new HandlerList();

    public WrappedEventController(RegisteredListener listener, long id, ControllerType type) {
        this.listener = listener;
        this.id = id;
        this.type = type;
    }

    public ControllerType getType() {
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
