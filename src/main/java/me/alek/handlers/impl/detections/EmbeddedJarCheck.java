package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Path;

public class EmbeddedJarCheck extends CheckAdapter implements DetectionNode {

    private String jarName;

    @Override
    public Pair<String, String> processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        String fileName = file.getName();
        if (!fileName.equals(jarName)) {
            if (fileName.endsWith(".jar") || fileName.endsWith(".zip") || fileName.endsWith(".jarinjar") || fileName.endsWith(".tar")) {
                return new Pair<>("", null);
            }
        }
        return null;
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        jarName = file.getName();
        return null;
    }

    @Override
    public String getType() {
        return "Embedded jar/zip";
    }

    @Override
    public Risk getRisk() {
        return Risk.MODERATE;
    }
}
