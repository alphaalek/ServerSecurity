package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.controllers.BytecodeController;
import me.alek.handlers.types.MethodInvokeHandler;
import me.alek.handlers.types.ParseHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class SystemPropertyCheck extends MethodInvokeHandler implements DetectionNode, ParseHandler {

    public SystemPropertyCheck() {
        super(MethodInsnNode.class);
    }

    private List<String> bannedProperties;

    @Override
    public void parse() {
        if (bannedProperties != null) return;
        this.bannedProperties = Arrays.asList(
                "java.home",
                //"os.arch",
                //"os.version",
                "user.dir",
                //"java.io.tmpdir",
                //"user.home",
                "user.name"
        );
    }
    @Override
    public String preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    private String check(String string) {
        for (String property : bannedProperties) {
            if (string.equals(property)) {
                return property;
            }
        }
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        if (!methodInsnNode.name.equals("getProperty")) return null;
        if (!methodInsnNode.owner.equals("java/lang/System")) return null;

        AbstractInsnNode previous = methodInsnNode.getPrevious();
        if (previous instanceof LdcInsnNode) {
            LdcInsnNode ldcInsnNode = (LdcInsnNode) previous;
            Object cst = ldcInsnNode.cst;
            if (!(cst instanceof String)) return null;
            return check((String)cst);
        }
        String encryptedString = BytecodeController.getStringUsed(methodInsnNode);
        if (encryptedString == null) return null;
        return check(encryptedString);
    }

    @Override
    public String getType() {
        return "System Property";
    }

    @Override
    public Risk getRisk() {
        return Risk.LOW;
    }

}
