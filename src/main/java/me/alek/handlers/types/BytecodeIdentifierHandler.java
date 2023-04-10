package me.alek.handlers.types;


import me.alek.handlers.CheckAdapter;
import me.alek.model.Pair;
import me.alek.utils.bytecodeidentifier.CharSerializer;
import me.alek.utils.bytecodeidentifier.LevenshteinDistanceRecursive;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class BytecodeIdentifierHandler extends CheckAdapter {
    @Override
    public Pair<String, String> processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        if (classNode == null) return null;
        ArrayList<AbstractInsnNode> instructions = new ArrayList<>();
        classNode.methods.forEach(method -> instructions.addAll(Arrays.asList(method.instructions.toArray())));
        if (instructions.size() < 25) return null;
        if (instructions.size() > 200) return null;

        StringBuilder bytecodeClassString = new StringBuilder();
        CharSerializer serializer = new CharSerializer();
        for (AbstractInsnNode abstractInsnNode : instructions) {
            int opcode = abstractInsnNode.getOpcode();
            if (opcode == -1) continue;
            bytecodeClassString.append(serializer.serializeInt(opcode));
        }
        LevenshteinDistanceRecursive metricsRecursive = new LevenshteinDistanceRecursive();
        int distance = metricsRecursive.calculate(bytecodeClassString.toString(), getBytecodeClassString());
        if (distance < 5) {
            return new Pair<>("", classPath.getFileName().toString());
        }
        return null;
    }

    public abstract String getBytecodeClassString();
}
