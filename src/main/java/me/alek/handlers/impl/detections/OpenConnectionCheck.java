package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.types.AbstractInstructionHandler;
import me.alek.handlers.types.OnlySourceLibraryHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;

public class OpenConnectionCheck extends AbstractInstructionHandler implements DetectionNode, OnlySourceLibraryHandler {

    public OpenConnectionCheck() {
        super(MethodInsnNode.class);
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        if (!methodInsnNode.name.equals("openConnection")) return null;
        if (!methodInsnNode.owner.startsWith("java/net/URL")) return null;
        return "";
    }

    @Override
    public String getType() {
        return "OpenConnection";
    }

    @Override
    public Risk getRisk() {
        return Risk.LOW;
    }
}
