package me.alek.handlers.impl.detections;


import me.alek.enums.Risk;
import me.alek.handlers.types.AbstractInstructionHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;

public class WebsocketCheck extends AbstractInstructionHandler implements DetectionNode {

    public WebsocketCheck() {
        super(MethodInsnNode.class, LdcInsnNode.class);
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    private String getVariant(String owner) {
        if (owner.equals("org/glassfish/tyrus/")) {
            return "Tyrus";
        }
        if (owner.contains("javax/websocket/")) {
            return "Javax";
        }
        if (owner.contains("org/springframework/web/socket")) {
            return "Spring";
        }
        if (owner.equals("java/nio/channels/ServerSocketChannel")
                || owner.equals("java/nio/channels/SocketChannel")) {
            return "Nio";
        }
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        if (abstractInsnNode instanceof MethodInsnNode) {
            MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
            String variant = getVariant(methodInsnNode.owner);
            if (variant == null) {
                return null;
            }
            return variant;
        } else {
            LdcInsnNode ldcInsnNode = (LdcInsnNode) abstractInsnNode;
            Object value = ldcInsnNode.cst;
            if (!(value instanceof String)) return null;
            String string = (String) value;
            if (!string.contains("ws://")) return null;
            return "Protocol";
        }
    }

    @Override
    public String getType() {
        return "Websocket";
    }

    @Override
    public Risk getRisk() {
        return Risk.MODERATE;
    }
}
