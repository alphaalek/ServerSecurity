package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.OnlySourceLibraryHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Path;

public class L10ClassCheck extends CheckAdapter implements DetectionNode, OnlySourceLibraryHandler {

    @Override
    public String processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        if (classPath.getFileName().toString().endsWith("L10.class")) {
            return "";
        }
        return null;
    }

    @Override
    public String preProcessJAR(File file, Path rootFolder) {
        return null;
    }

    @Override
    public String getType() {
        return "L10 Class";
    }

    @Override
    public Risk getRisk() {
        return Risk.LOW;
    }
}
