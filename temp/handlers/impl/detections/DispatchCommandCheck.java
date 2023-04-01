package me.alek.handlers.impl.detections;

import me.alek.controllers.BytecodeController;
import me.alek.enums.Risk;
import me.alek.handlers.types.InsnInvokeHandler;
import me.alek.handlers.types.OnlySourceLibraryHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.io.File;
import java.nio.file.Path;

public class DispatchCommandCheck extends InsnInvokeHandler implements DetectionNode, OnlySourceLibraryHandler {

    public DispatchCommandCheck() {
        super(MethodInsnNode.class);
    }

    @Override
    public String preProcessJAR(File file, Path rootFolder) {
        return null;
    }

    public boolean isExecutedByConsole(AbstractInsnNode abstractInsnNode) {
        AbstractInsnNode previous = abstractInsnNode;
        int i = 0;
        while ((previous = previous.getPrevious()) != null) {
            if (previous instanceof MethodInsnNode methodInsnNode) {
                if (methodInsnNode.desc.contains("org/bukkit/command/ConsoleCommandSender")) {
                    return true;
                }
            }
            if (i > 5) {
                return false;
            }
            i++;
        }
        return false;
    }

    @Override
    public String processAbstractInsn(AbstractInsnNode abstractInsnNode) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        String owner = methodInsnNode.owner;
        if (methodInsnNode.name.equals("dispatchCommand")) {
            switch (owner) {
                case "org/bukkit/Server", "org/bukkit/Bukkit":
                    if (isExecutedByConsole(methodInsnNode)) {
                        return "";
                    }
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return "Dispatch Command";
    }

    @Override
    public Risk getRisk() {
        return Risk.MODERATE;
    }
}
