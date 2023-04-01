package me.alek.handlers.types;

import me.alek.handlers.CheckAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;

import org.bukkit.Bukkit;

public abstract class InsnInvokeHandler extends CheckAdapter {

    private final Class<? extends AbstractInsnNode>[] insnTypes;

    @SafeVarargs
    public InsnInvokeHandler(Class<? extends AbstractInsnNode>... insnTypes) {
        this.insnTypes = insnTypes;
    }

    @Override
    public String processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        if (classNode == null) return null;
        for (MethodNode methodNode : classNode.methods) {
            for (AbstractInsnNode abstractInsnNode : methodNode.instructions) {
                for (Class<? extends AbstractInsnNode> insnNode : insnTypes) {

                    if (insnNode != abstractInsnNode.getClass()) continue;

                    String variant = processAbstractInsn(abstractInsnNode);
                    if (variant != null) {
                        return processAbstractInsn(abstractInsnNode);
                    }
                }
            }
        }
        return null;
    }

    public abstract String processAbstractInsn(AbstractInsnNode abstractInsnNode);
}
