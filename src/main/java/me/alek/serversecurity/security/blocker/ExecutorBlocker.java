package me.alek.serversecurity.security.blocker;

import me.alek.serversecurity.malware.BytecodeHelper;
import me.alek.serversecurity.logging.LogHolder;
import me.alek.serversecurity.security.operator.OperatorManager;
import org.apache.logging.log4j.Level;
import org.bukkit.event.Event;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutorBlocker<EVENT extends Event> {

    private HashMap<Blocker, DataContainer> blockers = null;

    private final AnnotationInjectedVisitor<EVENT> visitor;
    private final ExecutorDetector.PossibleMaliciousEventWrapper event;

    private static CommandProxy commandChannel;

    public ExecutorBlocker(AnnotationInjectedVisitor<EVENT> visitor, ExecutorDetector.PossibleMaliciousEventWrapper event) {
        this.visitor = visitor;
        this.event = event;

        commandChannel = CommandProxy.get();
    }

    private static class DataContainer {

        private String string;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }

    @FunctionalInterface
    private interface BiConsumer<T, U> {
        void accept(T t, U u);
    }

    private static String formatOperatorType(boolean type) {
        return (type ? "op" : "deop");
    }

    private enum Blocker {

        SET_OP("setOp", (data, event) -> {
            final OperatorManager operatorManager = OperatorManager.get();
            final List<OperatorManager.OpPlayerChange> latestOpChanges = operatorManager.getLatestOpChanges(Duration.ofMillis(500));

            for (OperatorManager.OpPlayerChange change : latestOpChanges) {

                LogHolder.getSecurityLogger().log(Level.WARN, "OP-skift fra cancelled chat event blokeret: " +
                        formatOperatorType(!change.isOp()) + " -> " + formatOperatorType(change.isOp()) + ", " +
                        change.getPlayer().getName() + " (" + change.getPlayer().getUniqueId() + ")");

                change.getPlayer().setOp(!change.isOp());
            }
        }),
        DISPATCH_COMMAND("dispatchCommand", (data, event) -> {
            final HashMap<String, CommandProxy.ResponseListener> requestedCommands = commandChannel.getRequestedCommands();
            if (requestedCommands.isEmpty()) {
                return;
            }
            for (CommandProxy.ResponseListener listener : requestedCommands.values()) {
                listener.onDeclined();
            }
        });


        private final String methodName;
        private final BiConsumer<DataContainer, ExecutorDetector.PossibleMaliciousEventWrapper> callbackConsumer;

        Blocker(String methodName, BiConsumer<DataContainer, ExecutorDetector.PossibleMaliciousEventWrapper> callbackConsumer) {
            this.methodName = methodName;
            this.callbackConsumer = callbackConsumer;
        }

        public String getMethodName() {
            return methodName;
        }

        public BiConsumer<DataContainer, ExecutorDetector.PossibleMaliciousEventWrapper> getCallbackConsumer() {
            return callbackConsumer;
        }
    }

    private DataContainer getDataContainer(Blocker blocker, MethodInsnNode methodInsnNode) {
        DataContainer container = new DataContainer();
        if (blocker == Blocker.DISPATCH_COMMAND) {

            String command = BytecodeHelper.getStrictStringUsedEntry(methodInsnNode);
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
            for (AbstractInsnNode abstractInsnNode : methodNode.instructions.toArray()) {

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

        return !blockers.isEmpty();
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

    public HashMap<Blocker, DataContainer> getBlockers() {
        return blockers;
    }
}
