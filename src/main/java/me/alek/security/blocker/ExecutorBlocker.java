package me.alek.security.blocker;

import lombok.Getter;
import lombok.Setter;
import me.alek.controllers.BytecodeController;
import me.alek.logging.LogHolder;
import me.alek.security.operator.OperatorManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ExecutorBlocker<EVENT extends Event> {

    @Getter private HashMap<Blocker, DataContainer> blockers = null;

    private final AnnotationInjectedVisitor<EVENT> visitor;
    private final ExecutorDetector.PossibleMaliciousEventWrapper event;

    private static CommandChannel commandChannel;

    public ExecutorBlocker(AnnotationInjectedVisitor<EVENT> visitor, ExecutorDetector.PossibleMaliciousEventWrapper event) {
        this.visitor = visitor;
        this.event = event;

        commandChannel = CommandChannel.get();
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

            final OperatorManager operatorManager = OperatorManager.get();
            final List<OperatorManager.OpPlayerChange> latestOpChanges = operatorManager.getLatestOpChanges(Duration.ofMillis(500));

            for (OperatorManager.OpPlayerChange change : latestOpChanges) {

                LogHolder.getSecurityLogger().log(Level.SEVERE, "OP-skift fra cancelled chat event blev blokeret: " + !change.isOp() + " -> " + change.isOp() + ", " + change.getPlayer().getName() + " (" + change.getPlayer().getUniqueId() + ")");
                change.getPlayer().setOp(!change.isOp());
            }

        }),
        DISPATCH_COMMAND("dispatchCommand", new BiConsumer<DataContainer, ExecutorDetector.PossibleMaliciousEventWrapper>() {

            @Override
            public void accept(DataContainer data, ExecutorDetector.PossibleMaliciousEventWrapper event) {

                final HashMap<String, CommandChannel.ResponseListener> requestedCommands = commandChannel.getRequestedCommands();

                if (requestedCommands.isEmpty()) {
                    return;
                }
                for (CommandChannel.ResponseListener listener : requestedCommands.values()) {
                    listener.onDeclined();
                }
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
            container.setString(command.split(" ")[0]);
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

    public void blockModules() {
        for (Map.Entry<Blocker, DataContainer> blocker : blockers.entrySet()) {
            blocker.getKey().getCallbackConsumer().accept(blocker.getValue(), event);
        }
    }

    public boolean process(String methodSignature) {
        MethodNode methodNode = getCorrespondingMethod(methodSignature);
        if (methodNode == null) {
            return false;
        }
        blockers = getRerverseModules(methodNode);
        if (blockers.isEmpty()) {
            return false;
        }

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
