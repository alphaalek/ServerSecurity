package me.alek.handlers.impl.detections;

import com.sun.jdi.Method;
import me.alek.enums.Risk;
import me.alek.handlers.types.InsnInvokeHandler;
import me.alek.handlers.types.OnlySourceLibraryHandler;
import me.alek.handlers.types.ParseHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import org.bukkit.Bukkit;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ForceOPCheck extends InsnInvokeHandler implements DetectionNode, ParseHandler {

    public ForceOPCheck() {
        super(MethodInsnNode.class, LdcInsnNode.class);
    }

    private List<String> methodInvokeOwners;

    @Override
    public void parse() {
        if (methodInvokeOwners != null) return;
        methodInvokeOwners = Arrays.asList(
                "org/bukkit/entity/Player",
                "org/bukkit/OfflinePlayer",
                "org/bukkit/command/CommandSender",
                "org/bukkit/permissions/ServerOperator"
        );
    }

    @Override
    public String preProcessJAR(File file, Path rootFolder) {
        return null;
    }

    @Override
    public String processAbstractInsn(AbstractInsnNode abstractInsnNode) {
        if (abstractInsnNode instanceof MethodInsnNode methodInsnNode) {
            String owner = methodInsnNode.owner;
            if (methodInsnNode.name.equals("setOp")) {
                for (String insnOwner : methodInvokeOwners) {
                    if (!owner.equals(insnOwner)) continue;
                    return "";
                }
            }
        }
        if (abstractInsnNode instanceof LdcInsnNode ldcInsnNode) {
            Object cst = ldcInsnNode.cst;
            if (!(cst instanceof String)) return null;
            if (((String)cst).contains("ops.json")) {
                return "ops.json";
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return "Force OP";
    }

    @Override
    public Risk getRisk() {
        return Risk.HIGH;
    }

}
