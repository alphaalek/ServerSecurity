package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.types.InsnInvokeHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.io.File;
import java.nio.file.Path;

public class SystemAccessCheck extends InsnInvokeHandler implements DetectionNode {

    public SystemAccessCheck() {
        super(MethodInsnNode.class);
    }

    @Override
    public String preProcessJAR(File file, Path rootFolder) {
        return null;
    }

    @Override
    public String processAbstractInsn(AbstractInsnNode abstractInsnNode) {
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
