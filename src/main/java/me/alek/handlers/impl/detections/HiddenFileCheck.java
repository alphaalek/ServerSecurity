package me.alek.handlers.impl.detections;

import lombok.SneakyThrows;
import me.alek.enums.Risk;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.nodes.DetectionNode;
import org.bukkit.Bukkit;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class HiddenFileCheck extends CheckAdapter implements DetectionNode {

    @Override
    public String processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        return null;
    }

    @SneakyThrows
    @Override
    public String preProcessJAR(File file, Path rootFolder) {
        if (Files.isHidden(file.toPath())) {
            return "";
        }
        if (file.isHidden()) {
            return "";
        }
        return null;
    }


    @Override
    public String getType() {
        return "Hidden File";
    }

    @Override
    public Risk getRisk() {
        return Risk.LOW;
    }
}
