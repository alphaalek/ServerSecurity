package me.alek.handlers.impl.detections;

import lombok.Getter;
import me.alek.AntiMalwarePlugin;
import me.alek.enums.Risk;
import me.alek.handlers.types.AbstractInstructionHandler;
import me.alek.handlers.types.ParseHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ForceOPCheck extends AbstractInstructionHandler implements DetectionNode, ParseHandler {

    @Getter
    private List<String> methodInvokeOwners;
    private PluginProperties pluginProperties;

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

    public ForceOPCheck() {
        super(MethodInsnNode.class, LdcInsnNode.class);
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        this.pluginProperties = pluginProperties;
        return null;
    }


    public static String formatPlugin(String plugin) {
        return plugin
                .replaceAll("\\.jar", "")
                .replaceAll("[_-]", "")
                .replaceAll("[0-9]", "")
                .replaceAll("\\.", "");
    }

    public static boolean validatePluginAcceptance(String plugin, List<String> checks) {
        String formattedPlugin = formatPlugin(plugin.toLowerCase());
        for (String checkString : checks) {
            if (formattedPlugin.replaceAll(checkString.toLowerCase(), "").length() < 3 && formattedPlugin.length() >= 3) {
                return true;
            }
        }
        return false;
    }

    private boolean validatePluginAcceptance(Path classPath) {
        if (pluginProperties == null) return false;
        if (pluginProperties.getSourceLib() == null) return false;
        if (pluginProperties.getPluginName() == null) return false;

        if (classPath.toAbsolutePath().toString().contains(pluginProperties.getSourceLib())) {
            return validatePluginAcceptance(pluginProperties.getPluginName(), AntiMalwarePlugin.getAcceptedPluginsForceOPContainer().getList());
        }
        return false;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        if (abstractInsnNode instanceof MethodInsnNode) {
            MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
            String owner = methodInsnNode.owner;
            if (methodInsnNode.name.equals("setOp")) {
                for (String insnOwner : methodInvokeOwners) {
                    if (!owner.equals(insnOwner)) continue;
                    if (validatePluginAcceptance(classPath)) continue;
                    return "";
                }
            }
        }
        if (abstractInsnNode instanceof LdcInsnNode) {
            LdcInsnNode ldcInsnNode =(LdcInsnNode) abstractInsnNode;
            Object cst = ldcInsnNode.cst;
            if (!(cst instanceof String)) return null;
            if (!((String)cst).contains("ops.json")) return null;
            return "ops.json";

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
