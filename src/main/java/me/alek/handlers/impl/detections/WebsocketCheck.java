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

public class WebsocketCheck extends AbstractInstructionHandler implements DetectionNode {

    public WebsocketCheck() {
        super(MethodInsnNode.class);
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    private String getVariant(String owner) {
        if (owner.equals("org/glassfish/tyrus/")) {
            return "";
        }
        if (owner.contains("javax/websocket/")) {
            return "";
        }
        /*if (owner.equals("java/net/InetSocketAddress")
                || owner.equals("java/net/ServerSocket")
                || owner.equals("java/net/Socket")
                || owner.equals("java/net/SocketAddress")) {
            return "";
        }*/
        if (owner.equals("java/nio/channels/ServerSocketChannel")
                || owner.equals("java/nio.channels/SocketChannel")) {
            return "";
        }

        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        String variant = getVariant(methodInsnNode.owner);
        if (variant == null) {
            return null;
        }
        return variant;
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
