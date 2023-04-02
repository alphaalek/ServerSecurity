package me.alek.handlers.types;

import me.alek.handlers.CheckAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;

public abstract class MethodInvokeHandler extends CheckAdapter {

    private final Class<? extends AbstractInsnNode>[] insnTypes;

    @SafeVarargs
    public MethodInvokeHandler(Class<? extends AbstractInsnNode>... insnTypes) {
        this.insnTypes = insnTypes;
    }

    @Override
    public String processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        if (classNode == null) return null;
        for (MethodNode methodNode : classNode.methods) {
            for (AbstractInsnNode abstractInsnNode : methodNode.instructions) {
                for (Class<? extends AbstractInsnNode> insnNode : insnTypes) {

                    if (insnNode != abstractInsnNode.getClass()) continue;

                    String variant = processAbstractInsn(methodNode, abstractInsnNode, classPath);
                    if (variant != null) {
                        return variant;
                    }
                }
            }
        }
        return null;
    }

    public abstract String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath);
}
