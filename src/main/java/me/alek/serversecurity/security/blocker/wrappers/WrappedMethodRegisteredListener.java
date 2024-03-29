package me.alek.serversecurity.security.blocker.wrappers;

import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public class WrappedMethodRegisteredListener extends RegisteredListener {

    private final String methodSignature;

    public WrappedMethodRegisteredListener(Listener listener, EventExecutor eventExecutor, EventPriority priority, Plugin plugin, Method method, boolean isIgnoringCancelled) {
        super(listener, eventExecutor, priority, plugin, isIgnoringCancelled);
        this.methodSignature = method.getName() + Type.getMethodDescriptor(method);
    }

    public String getMethodSignature() {
        return methodSignature;
    }
}


