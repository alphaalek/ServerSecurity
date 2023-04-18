package me.alek.security.event;

import me.alek.security.event.wrappers.WrappedMethodRegisteredListener;
import org.bukkit.plugin.RegisteredListener;

public class RegisteredListenerAdapter {

    private final RegisteredListener listener;

    public RegisteredListenerAdapter(RegisteredListener listener) {
        this.listener = listener;
    }

    public boolean isWrappedMethodListener() {
        return (listener instanceof WrappedMethodRegisteredListener);
    }

    public WrappedMethodRegisteredListener getWrappedMethodListener() {
        return (WrappedMethodRegisteredListener) listener;
    }

}
