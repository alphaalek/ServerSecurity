package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.OnlySourceLibraryHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Path;

public class L10ClassCheck extends CheckAdapter implements DetectionNode, OnlySourceLibraryHandler {

    @Override
    public Pair<String, String> processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        String className = classPath.getFileName().toString();
        if (className.endsWith("L10.class")) {
            return new Pair<>("", className);
        }
        return null;
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    @Override
    public String getType() {
        return "L10 Class";
    }

    @Override
    public Risk getRisk() {
        return Risk.MODERATE;
    }
}
