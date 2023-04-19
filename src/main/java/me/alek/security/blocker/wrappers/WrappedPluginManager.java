package me.alek.security.blocker.wrappers;

import me.alek.AntiMalwarePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.*;
import org.objectweb.asm.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class WrappedPluginManager implements PluginManager {

    private final PluginManager delegate;
    private final WrappedCommandMap wrappedCommandMap;
    private final HashMap<Listener, List<WrappedMethodRegisteredListener>> methodListeners = new HashMap<>();

    public WrappedPluginManager(PluginManager delegate) {
        WrappedCommandMap wrappedCommandMap1 = null;
        this.delegate = delegate;
        try {
            Class<?> simplePluginManagerClass = Class.forName("org.bukkit.plugin.SimplePluginManager");
            Field commandMapField = simplePluginManagerClass.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            // Create a new instance of WrappedCommandMap
            final CommandMap originalCommandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            wrappedCommandMap1 = new WrappedCommandMap((SimpleCommandMap) originalCommandMap);

            // Use ASM to replace all references to SimpleCommandMap with WrappedCommandMap
            ClassReader classReader = new ClassReader(simplePluginManagerClass.getName());
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5, classWriter) {
                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    if (descriptor.equals("Lorg/bukkit/command/SimpleCommandMap;")) {
                        descriptor = "Lorg/bukkit/command/CommandMap;";
                    }
                    return super.visitField(access, name, descriptor, signature, value);
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    return new MethodVisitor(Opcodes.ASM5, mv) {
                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                            if (descriptor.equals("Lorg/bukkit/command/SimpleCommandMap;")) {
                                descriptor = "Lorg/bukkit/command/CommandMap;";
                            }
                            super.visitFieldInsn(opcode, owner, name, descriptor);
                        }
                    };
                }
            };
            classReader.accept(classVisitor, ClassReader.SKIP_FRAMES);

            byte[] newClassBytes = classWriter.toByteArray();

            // Define the modified class in the same ClassLoader as the original class
            ClassLoader classLoader = simplePluginManagerClass.getClassLoader();
            Class<?> modifiedClass = (Class<?>) ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class)
                    .invoke(classLoader, simplePluginManagerClass.getName(), newClassBytes, 0, newClassBytes.length);

            // Create a new instance of the modified class and set its commandMap field to our wrappedCommandMap
            Object originalPluginManager = Bukkit.getServer().getPluginManager();
            Object modifiedPluginManager = modifiedClass.getConstructor(PluginLoader.class, SimplePluginManager.class)
                    .newInstance(originalPluginManager.getClass().getMethod("getPluginLoaders").invoke(originalPluginManager),
                            originalPluginManager);
            commandMapField.set(modifiedPluginManager, wrappedCommandMap1);

            // Replace the original plugin manager with the modified one
            Field pluginManagerField = Bukkit.getServer().getClass().getDeclaredField("pluginManager");
            pluginManagerField.setAccessible(true);
            pluginManagerField.set(Bukkit.getServer(), modifiedPluginManager);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        wrappedCommandMap = wrappedCommandMap1;
    }

    public CommandMap getCommandMap() {
        return wrappedCommandMap;
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
            if (methodListeners.containsKey(listener)) {
                if (methodListeners.get(listener).stream().anyMatch(listenerCheck -> listenerCheck.getMethodSignature().equals(method.getName()))) {
                    return;
                }
            } else {
                methodListeners.put(listener, new ArrayList<>());
            }
            WrappedMethodRegisteredListener wrappedListener;
            try {
                wrappedListener = new WrappedMethodRegisteredListener(listener, executor, eventPriority, plugin, method, b);
            } catch (NoSuchMethodException ex) {
                return;
            }
            AntiMalwarePlugin.getInstance().getLogger().info("REGISTER CHAT " + listener.getClass().getName() + " " + method.getName());
            handlers.register(wrappedListener);

            methodListeners.get(listener).add(wrappedListener);
            return;
        }
        AntiMalwarePlugin.getInstance().getLogger().info("REGISTER OTHER " + listener.getClass().getName() + " " + method.getName());
        delegate.registerEvent(eventClass, listener, eventPriority, executor, plugin, b);
/*
        RegisteredListener notChatListener;
        if (useTimings()) {
            notChatListener = new TimedRegisteredListener(listener, eventExecutor, eventPriority, plugin, b);
        } else {
            notChatListener = new RegisteredListener(listener, eventExecutor, eventPriority, plugin, b);
        }
        handlers.register(notChatListener);*/
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
