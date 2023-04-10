package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.types.AbstractInstructionHandler;
import me.alek.handlers.types.OnlySourceLibraryHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.bukkit.Bukkit;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;

public class IPGrapperCheck extends AbstractInstructionHandler implements DetectionNode {

    File file;

    public IPGrapperCheck() {
        super(MethodInsnNode.class, LdcInsnNode.class);
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        this.file = file;
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        if (abstractInsnNode instanceof MethodInsnNode) {
            MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
            if (!methodInsnNode.owner.equals("org/bukkit/entity/Player")) return null;
            if (!methodInsnNode.name.equals("getAddress")) return null;
            return "";
        } else {
            LdcInsnNode ldcInsnNode = (LdcInsnNode) abstractInsnNode;
            Object value = ldcInsnNode.cst;
            if (!(value instanceof String)) return null;
            String string = (String) value;
            if (!string.contains("api.ipify.org")) return null;
            return "api.ipify";
        }

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
