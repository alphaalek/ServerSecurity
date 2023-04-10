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

public class Base64Check extends AbstractInstructionHandler implements DetectionNode, OnlySourceLibraryHandler {

    public Base64Check() {
        super(MethodInsnNode.class);
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        String owner = methodInsnNode.owner;
        if (owner.equals("java/util/Base64$Decoder") || owner.equals("java/utils/Base64$Encoder") || owner.equals("org/apache/commons/codec/binary/Base64")) {
            return "";
        }
        return null;
    }

    @Override
    public String getType() {
        return "Base64";
    }

    @Override
    public Risk getRisk() {
        return Risk.LOW;
    }

}
