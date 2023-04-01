package me.alek.handlers.impl.detections;

import me.alek.controllers.BytecodeController;
import me.alek.enums.Risk;
import me.alek.handlers.types.InsnInvokeHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import org.bukkit.Bukkit;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class UserAgentRequestCheck extends InsnInvokeHandler implements DetectionNode {

    public UserAgentRequestCheck() {
        super(MethodInsnNode.class);
    }

    @Override
    public String preProcessJAR(File file, Path rootFolder) {
        return null;
    }

    @Override
    public String processAbstractInsn(AbstractInsnNode abstractInsnNode) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        if (!methodInsnNode.name.equals("addRequestProperty")) return null;
        if (!(methodInsnNode.owner.equals("javax/net/ssl/HttpsURLConnection") || methodInsnNode.owner.equals("java/net/HttpURLConnection"))) return null;
        String[] param = BytecodeController.getStringsUsed(abstractInsnNode, 2);
        if (Arrays.stream(param).filter(Objects::nonNull).toList().size() != param.length) return null;
        if (!(param[1].equalsIgnoreCase("User-Agent"))) return null;
        return "";
    }

    @Override
    public String getType() {
        return "User-Agent Request";
    }

    @Override
    public Risk getRisk() {
        return Risk.MODERATE;
    }
}
