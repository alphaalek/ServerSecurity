package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.types.AbstractInstructionHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;

public class SystemAccessCheck extends AbstractInstructionHandler implements DetectionNode {

    public SystemAccessCheck() {
        super(MethodInsnNode.class);
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        String name = methodInsnNode.name;
        if (!methodInsnNode.owner.equals("java/lang/Runtime")) return null;
        if (name.equals("exec")) {
            return "Exec";
        } if (name.equals("load") || name.equals("loadLibrary"))
            return "Load";
        return null;
    }

    @Override
    public String getType() {
        return "System Access";
    }

    @Override
    public Risk getRisk() {
        return Risk.MODERATE;
    }
}
