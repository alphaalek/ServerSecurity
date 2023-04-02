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

public class IPGrapperCheck extends MethodInvokeHandler implements DetectionNode, OnlySourceLibraryHandler {


    public IPGrapperCheck() {
        super(MethodInsnNode.class);
    }

    @Override
    public String preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        String owner = methodInsnNode.owner;
        String name = methodInsnNode.name;
        if (!owner.equals("org/bukkit/entity/Player")) return null;
        if (!name.equals("getAddress")) return null;
        return "";
    }

    @Override
    public String getType() {
        return "IP Grabber";
    }

    @Override
    public Risk getRisk() {
        return Risk.LOW;
    }
}
