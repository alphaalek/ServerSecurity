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

public class CipherEncryptionCheck extends AbstractInstructionHandler implements DetectionNode {

    public CipherEncryptionCheck() {
        super(MethodInsnNode.class);
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        if (!methodInsnNode.owner.equals("javax/crypto/Cipher")) return null;
        //if (!methodInsnNode.name.equals("getInstance")) return null;
        return "";
    }

    @Override
    public String getType() {
        return "Cipher Encryption";
    }

    @Override
    public Risk getRisk() {
        return Risk.LOW;
    }
}
