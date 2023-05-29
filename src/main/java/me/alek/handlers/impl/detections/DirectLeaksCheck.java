package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.types.AbstractInstructionHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;

public class DirectLeaksCheck extends AbstractInstructionHandler implements DetectionNode {

    public DirectLeaksCheck() {
        super(LdcInsnNode.class);
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        LdcInsnNode ldcInsnNode = (LdcInsnNode) abstractInsnNode;
        Object value = ldcInsnNode.cst;
        if (!(value instanceof String)) return null;
        String string = (String) value;
        if (!string.contains("DirectLeaks")) return null;
        return "";
    }

    @Override
    public String getType() {
        return "DirectLeaks";
    }

    @Override
    public Risk getRisk() {
        return Risk.MODERATE;
    }
}