package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.nodes.DetectionNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Path;

public class BytecodeManipulationCheck extends CheckAdapter implements DetectionNode {
    @Override
    public String processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        return null;
    }

    @Override
    public String preProcessJAR(File file, Path rootFolder) {
        if (resolve(rootFolder, "javassist/")) {
            return "Javassist";
        }
        if (resolve(rootFolder, "org/objectweb/asm/")) {
            return "ow2 ASM";
        }
        if (resolve(rootFolder, "net/bytebuddy/")) {
            return "Bytebuddy";
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
