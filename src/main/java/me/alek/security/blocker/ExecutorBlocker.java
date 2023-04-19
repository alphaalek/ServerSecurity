package me.alek.security.blocker;

import lombok.Getter;
import lombok.Setter;
import me.alek.AntiMalwarePlugin;
import me.alek.controllers.BytecodeController;
import me.alek.security.blocker.wrappers.WrappedCommandMap;
import me.alek.security.blocker.wrappers.WrappedPluginManager;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ExecutorBlocker<EVENT extends Event> {

    private final AnnotationInjectedVisitor<EVENT> visitor;
    private final ExecutorDetector.PossibleMaliciousEventWrapper event;

    private static WrappedCommandMap wrappedCommandMap;

    public ExecutorBlocker(AnnotationInjectedVisitor<EVENT> visitor, ExecutorDetector.PossibleMaliciousEventWrapper event) {
        this.visitor = visitor;
        this.event = event;

        wrappedCommandMap = getWrappedCommandMap();
    }

    private WrappedCommandMap getWrappedCommandMap() {
        final PluginManager pluginManager = AntiMalwarePlugin.getInstance().getServer().getPluginManager();
        if (pluginManager instanceof WrappedPluginManager) {
            final WrappedPluginManager wrappedPluginManager = (WrappedPluginManager) pluginManager;
            final CommandMap commandMap = wrappedPluginManager.getCommandMap();
            if (commandMap instanceof WrappedCommandMap) {
                return (WrappedCommandMap) commandMap;
            }
        }
        return null;
    }

    private static class DataContainer {

        @Getter @Setter private String string;

    }

    @FunctionalInterface
    private interface BiConsumer<T, U> {
        void accept(T t, U u);
    }

    private enum Blocker {

        SET_OP("setOp", (data, event) -> {
            event.getPlayer().setOp(false);
        }),
        DISPATCH_COMMAND("dispatchCommand", new BiConsumer<DataContainer, ExecutorDetector.PossibleMaliciousEventWrapper>() {

            @Override
            public void accept(DataContainer data, ExecutorDetector.PossibleMaliciousEventWrapper event) {

                final HashMap<String, WrappedCommandMap.ResponseListener> requestedCommands = wrappedCommandMap.getRequestedCommands();
                final Map.Entry<String, WrappedCommandMap.ResponseListener> matchedListener = requestedCommands.entrySet().stream()
                        .filter(entry -> entry.getValue().getCommandLine().contains(data.getString())).findFirst().orElse(null);
                final Predicate<Map.Entry<String, WrappedCommandMap.ResponseListener>> predicate;

                if (matchedListener != null) {
                    matchedListener.getValue().onDeclined();
                    predicate = (entry) -> !entry.getKey().equals(matchedListener.getKey());
                } else {
                    predicate = (entry) -> true;
                }
                requestedCommands.entrySet().stream().filter(predicate).forEach(entry -> {
                    entry.getValue().onAccepted();
                    requestedCommands.remove(entry.getKey());
                });
            }
        });


        @Getter private final String methodName;
        @Getter private final BiConsumer<DataContainer, ExecutorDetector.PossibleMaliciousEventWrapper> callbackConsumer;

        Blocker(String methodName, BiConsumer<DataContainer, ExecutorDetector.PossibleMaliciousEventWrapper> callbackConsumer) {
            this.methodName = methodName;
            this.callbackConsumer = callbackConsumer;
        }
    }

    private DataContainer getDataContainer(Blocker blocker, MethodInsnNode methodInsnNode) {
        DataContainer container = new DataContainer();
        if (blocker == Blocker.DISPATCH_COMMAND) {
            String command = BytecodeController.getStrictStringUsedEntry(methodInsnNode);
            if (command == null) {
                return null;
            }
            container.setString(command);
            return container;
        }
        return null;
    }

    private HashMap<Blocker, DataContainer> getRerverseModules(MethodNode methodNode) {

        final HashMap<Blocker, DataContainer> blockers = new HashMap<>();
        for (Blocker blocker : Blocker.values()) {

            for (AbstractInsnNode abstractInsnNode : methodNode.instructions) {
                if (abstractInsnNode instanceof MethodInsnNode) {

                    MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                    if (blocker.getMethodName().equals(methodInsnNode.name)) {
                        blockers.put(blocker, getDataContainer(blocker, methodInsnNode));
                    }
                }
            }
        }
        return blockers;
    }

    private void blockModules(HashMap<Blocker, DataContainer> blockers) {
        for (Map.Entry<Blocker, DataContainer> blocker : blockers.entrySet()) {
            blocker.getKey().getCallbackConsumer().accept(blocker.getValue(), event);
        }
    }

    public boolean process(String methodSignature) {
        MethodNode methodNode = getCorrespondingMethod(methodSignature);
        if (methodNode == null) {
            return false;
        }
        final HashMap<Blocker, DataContainer> blockers = getRerverseModules(methodNode);
        if (blockers.isEmpty()) {
            return false;
        }
        blockModules(blockers);

        return true;
    }

    private String getMethodSignature(Method method) {
        return method.getName() + Type.getMethodDescriptor(method);
    }

    private MethodNode getCorrespondingMethod(String methodSignature) {
        for (Map.Entry<String, MethodNode> entry : visitor.getAnnotatedMethodNodeMap().entrySet()) {

            if (entry.getKey().equals(methodSignature)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
