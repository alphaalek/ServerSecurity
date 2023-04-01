package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.types.InsnInvokeHandler;
import me.alek.handlers.types.OnlySourceLibraryHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.io.File;
import java.nio.file.Path;

public class LoadPluginCheck extends InsnInvokeHandler implements DetectionNode, OnlySourceLibraryHandler {

    public LoadPluginCheck() {
        super(MethodInsnNode.class);
    }

    @Override
    public String preProcessJAR(File file, Path rootFolder) {
        return null;
    }

    @Override
    public String processAbstractInsn(AbstractInsnNode abstractInsnNode) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        String name = methodInsnNode.name;
        if (!methodInsnNode.owner.equals("org/bukkit/plugin/PluginManager")) return null;
        if (!(name.equals("loadPlugin") || name.equals("loadPlugins"))) return null;
        return "";
    }

    @Override
    public String getType() {
        return "Load Plugins";
    }

    @Override
    public Risk getRisk() {
        return Risk.LOW;
    }
}
