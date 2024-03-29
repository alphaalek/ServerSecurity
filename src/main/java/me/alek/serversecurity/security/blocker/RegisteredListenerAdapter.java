package me.alek.serversecurity.security.blocker;

import me.alek.serversecurity.security.blocker.wrappers.WrappedMethodRegisteredListener;
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
        if (!isWrappedMethodListener()) return null;
        return (WrappedMethodRegisteredListener) listener;
    }

}
