package me.alek.handlers.types;



import me.alek.controllers.BytecodeController;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class RequestPropertyHandler extends AbstractInstructionHandler implements DetectionNode{

    public RequestPropertyHandler() {
        super(MethodInsnNode.class);
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        if (!methodInsnNode.name.equals("addRequestProperty")) return null;
        if (!(methodInsnNode.owner.equals("javax/net/ssl/HttpsURLConnection") || methodInsnNode.owner.equals("java/net/HttpURLConnection"))) return null;

        String[] param = BytecodeController.getStringsUsed(abstractInsnNode, 2);
        if (param == null) return null;
        if (Arrays.stream(param).filter(Objects::isNull).collect(Collectors.toList()).size() == param.length) return null;

        int i = 0;
        for (String paramCheck : getParams()) {
            if (!paramCheck.equals("")) {
                if (param[i] == null) return null;
                if (!param[i].equalsIgnoreCase(paramCheck)) return null;
            }
            i++;
        }
        return "";
    }

    public abstract String[] getParams();
}
