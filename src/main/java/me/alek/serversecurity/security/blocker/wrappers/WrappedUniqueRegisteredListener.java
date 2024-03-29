package me.alek.serversecurity.security.blocker.wrappers;

import me.alek.serversecurity.security.blocker.RegisteredListenerAdapter;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

public class WrappedUniqueRegisteredListener extends RegisteredListener {


    private final RegisteredListenerAdapter adapter;
    public final RegisteredListener delegate;
    private long id = 0;

    public WrappedUniqueRegisteredListener(RegisteredListener delegate) {
        super(delegate.getListener(), null, delegate.getPriority(), delegate.getPlugin(), false);
        adapter = new RegisteredListenerAdapter(delegate);
        this.delegate = delegate;
    }

    public void callEvent(Event event) throws EventException {
        this.delegate.callEvent(event);
    }

    public RegisteredListenerAdapter getAdapter() {
        return adapter;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Listener getListener() {
        return delegate.getListener();
    }

    public Plugin getPlugin() {
        return delegate.getPlugin();
    }

    public EventPriority getPriority() {
        return delegate.getPriority();
    }

    public boolean isIgnoringCancelled() {
        return delegate.isIgnoringCancelled();
    }
}
