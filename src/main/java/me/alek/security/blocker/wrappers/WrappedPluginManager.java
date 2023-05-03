package me.alek.security.blocker.wrappers;

import org.bukkit.command.CommandMap;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class WrappedPluginManager implements PluginManager {

    private final PluginManager delegate;
    private final HashMap<Listener, ArrayList<WrappedMethodRegisteredListener>> registeredChatListeners = new HashMap<>();
    private final HashMap<Listener, ArrayList<Method>> registeredListeners = new HashMap<>();

    public WrappedPluginManager(PluginManager delegate) {
        this.delegate = delegate;
    }

    private static HandlerList getHandlerList(Class<? extends Event> clazz) throws Exception {
        while (clazz.getSuperclass() != null && Event.class.isAssignableFrom(clazz.getSuperclass())) {
            try {
                Method method = clazz.getDeclaredMethod("getHandlerList");
                method.setAccessible(true);
                return (HandlerList) method.invoke(null);
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass().asSubclass(Event.class);
            }
        }
        throw new Exception();
    }

    @Override
    public void registerEvents(Listener listener, Plugin plugin) {
        try {
            registerAll(listener, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerEvent(Class<? extends Event> aClass, Listener listener, EventPriority eventPriority, EventExecutor eventExecutor, Plugin plugin) {
        if (!aClass.isAssignableFrom(AsyncPlayerChatEvent.class)) delegate.registerEvent(aClass, listener, eventPriority, eventExecutor, plugin);
        try {
            registerAll(listener, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerEvent(Class<? extends Event> aClass, Listener listener, EventPriority eventPriority, EventExecutor eventExecutor, Plugin plugin, boolean b) {
        if (!aClass.isAssignableFrom(AsyncPlayerChatEvent.class)) delegate.registerEvent(aClass, listener, eventPriority, eventExecutor, plugin, b);
        try {
            registerAll(listener, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerAll(Listener listener, Plugin plugin) throws Exception {
        Method[] methods;
        try {
            methods = listener.getClass().getDeclaredMethods();
        } catch (NoClassDefFoundError e) {
            methods = new Method[0];
        }
        for (Method method : methods) {
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            if (eventHandler == null) continue;
            if (!registeredListeners.containsKey(listener)) {
                registeredListeners.put(listener, new ArrayList<>());
            }
            if (registeredListeners.get(listener).contains(method)) {
                continue;
            }
            registeredListeners.get(listener).add(method);

            Class<? extends Event> checkClass = method.getParameterTypes()[0].asSubclass(Event.class);
            registerEventToHandlers(checkClass, listener, eventHandler.priority(), plugin, method, eventHandler.ignoreCancelled());
        }
    }

    private void registerEventToHandlers(Class<? extends Event> eventClass, Listener listener, EventPriority eventPriority, Plugin plugin, Method method, boolean b) throws Exception {

        HandlerList handlers = getHandlerList(eventClass);
        method.setAccessible(true);
        EventExecutor executor = new EventExecutor() {
            public void execute(Listener listener, Event event) throws EventException {
                try {
                    if (!eventClass.isAssignableFrom(event.getClass())) {
                        return;
                    }
                    method.invoke(listener, event);
                } catch (InvocationTargetException ex) {
                    throw new EventException(ex.getCause());
                } catch (Throwable t) {
                    throw new EventException(t);
                }
            }
        };
        if (eventClass.isAssignableFrom(AsyncPlayerChatEvent.class)) {
            if (registeredChatListeners.containsKey(listener)) {
                if (registeredChatListeners.get(listener).stream().anyMatch(listenerCheck -> listenerCheck.getMethodSignature().equals(method.getName()))) {
                    return;
                }
            } else {
                registeredChatListeners.put(listener, new ArrayList<>());
            }
            WrappedMethodRegisteredListener wrappedListener;
            try {
                wrappedListener = new WrappedMethodRegisteredListener(listener, executor, eventPriority, plugin, method, b);
            } catch (NoSuchMethodException ex) {
                return;
            }
            handlers.register(wrappedListener);

            registeredChatListeners.get(listener).add(wrappedListener);
        } else {
            /*HandlerList.getHandlerLists().stream()
                    .filter(handlerList -> Arrays.stream(handlerList.getRegisteredListeners())
                            .noneMatch(registeredListener -> registeredListener.getListener().equals(listener)))
                    .findFirst().orElse(handlers)
                    .register(new WrappedMethodRegisteredListener(listener, executor, eventPriority, plugin, method, b));*/
            RegisteredListener notChatListener;
            if (useTimings()) {
                notChatListener = new TimedRegisteredListener(listener, executor, eventPriority, plugin, b);
            } else {
                notChatListener = new RegisteredListener(listener, executor, eventPriority, plugin, b);
            }
            handlers.register(notChatListener);
        }
    }

    @Override
    public void registerInterface(Class<? extends PluginLoader> aClass) throws IllegalArgumentException {
        delegate.registerInterface(aClass);
    }

    @Override
    public Plugin getPlugin(String s) {
        return delegate.getPlugin(s);
    }

    @Override
    public Plugin[] getPlugins() {
        return delegate.getPlugins();
    }

    @Override
    public boolean isPluginEnabled(String s) {
        return delegate.isPluginEnabled(s);
    }

    @Override
    public boolean isPluginEnabled(Plugin plugin) {
        return delegate.isPluginEnabled(plugin);
    }

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException, InvalidDescriptionException, UnknownDependencyException {
        return delegate.loadPlugin(file);
    }

    @Override
    public Plugin[] loadPlugins(File file) {
        return delegate.loadPlugins(file);
    }

    @Override
    public void disablePlugins() {
        delegate.disablePlugins();
    }

    @Override
    public void clearPlugins() {
        delegate.clearPlugins();
    }

    @Override
    public void callEvent(Event eventClass) throws IllegalStateException {
        delegate.callEvent(eventClass);
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        delegate.enablePlugin(plugin);
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        delegate.disablePlugin(plugin);
    }

    @Override
    public Permission getPermission(String s) {
        return delegate.getPermission(s);
    }

    @Override
    public void addPermission(Permission permission) {
        delegate.addPermission(permission);
    }

    @Override
    public void removePermission(Permission permission) {
        delegate.removePermission(permission);
    }

    @Override
    public void removePermission(String s) {
        delegate.removePermission(s);
    }

    @Override
    public Set<Permission> getDefaultPermissions(boolean b) {
        return delegate.getDefaultPermissions(b);
    }

    @Override
    public void recalculatePermissionDefaults(Permission permission) {
        delegate.recalculatePermissionDefaults(permission);
    }

    @Override
    public void subscribeToPermission(String s, Permissible permissible) {
        delegate.subscribeToPermission(s, permissible);
    }

    @Override
    public void unsubscribeFromPermission(String s, Permissible permissible) {
        delegate.unsubscribeFromPermission(s, permissible);
    }

    @Override
    public Set<Permissible> getPermissionSubscriptions(String s) {
        return delegate.getPermissionSubscriptions(s);
    }

    @Override
    public void subscribeToDefaultPerms(boolean b, Permissible permissible) {
        delegate.subscribeToDefaultPerms(b, permissible);
    }

    @Override
    public void unsubscribeFromDefaultPerms(boolean b, Permissible permissible) {
        delegate.unsubscribeFromDefaultPerms(b, permissible);
    }

    @Override
    public Set<Permissible> getDefaultPermSubscriptions(boolean b) {
        return delegate.getDefaultPermSubscriptions(b);
    }

    @Override
    public Set<Permission> getPermissions() {
        return delegate.getPermissions();
    }

    @Override
    public boolean useTimings() {
        return delegate.useTimings();
    }
}
