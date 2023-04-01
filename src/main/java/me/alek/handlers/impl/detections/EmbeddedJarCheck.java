package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.nodes.DetectionNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Path;

public class EmbeddedJarCheck extends CheckAdapter implements DetectionNode {

    private String jarName;

    @Override
    public String processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        String fileName = file.getName();
        if (!fileName.equals(jarName)) {
            if (fileName.endsWith(".jar") || fileName.endsWith(".zip") || fileName.endsWith(".jarinjar") || fileName.endsWith(".tar")) {
                return "";
            }
        }
        return null;
    }

    @Override
    public String preProcessJAR(File file, Path rootFolder) {
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
