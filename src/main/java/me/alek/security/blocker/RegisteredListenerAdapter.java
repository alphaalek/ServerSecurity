package me.alek.security.blocker;

import me.alek.security.blocker.wrappers.WrappedMethodRegisteredListener;
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
