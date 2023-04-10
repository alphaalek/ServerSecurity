package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Path;

public class BytecodeManipulationCheck extends CheckAdapter implements DetectionNode {
    @Override
    public Pair<String, String> processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        return null;
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        if (resolve(rootFolder, "javassist/")) {
            return new Pair<>("Javassist", null);
        }
        if (resolve(rootFolder, "org/objectweb/asm/")) {
            return new Pair<>("ow2 ASM", null);
        }
        if (resolve(rootFolder, "net/bytebuddy/")) {
            return new Pair<>("Bytebuddy", null);
        }
        return null;
    }

    @Override
    public String getType() {
        return "BCM";
    }

    @Override
    public Risk getRisk() {
        return Risk.HIGH;
    }
}
