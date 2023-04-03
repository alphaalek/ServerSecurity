package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.types.MethodInvokeHandler;
import me.alek.handlers.types.OnlySourceLibraryHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;

public class DispatchCommandCheck extends MethodInvokeHandler implements DetectionNode {

    public DispatchCommandCheck() {
        super(MethodInsnNode.class);
    }

    @Override
    public String preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    public String isValid(MethodNode methodNode, AbstractInsnNode abstractInsnNode) {
        AbstractInsnNode previous = abstractInsnNode;
        int i = 0;

        String name = methodNode.name.toLowerCase();
        if (name.contains("chat")) { //|| name.contains("command") || name.contains("execute")) {
            return "";
        }
        String desc = methodNode.desc;
        if (desc.contains("AsyncPlayerChatEvent")
                || desc.contains("ConsoleCommandSender")) {
            return "";
        }

        while ((previous = previous.getPrevious()) != null) {
            if (previous instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) previous;
                if (methodInsnNode.desc.contains("ConsoleCommandSender")) {
                    return "";
                }
            }
            if (i > 5) {
                return null;
            }
            i++;
        }
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        String owner = methodInsnNode.owner;
        if (!methodInsnNode.name.equals("dispatchCommand")) return null;
        if (!(owner.equals("org/bukkit/Server") || owner.equals("org/bukkit/Bukkit"))) return null;
        return isValid(methodNode, methodInsnNode);
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
