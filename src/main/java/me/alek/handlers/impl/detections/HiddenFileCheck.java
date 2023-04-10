package me.alek.handlers.impl.detections;

import lombok.SneakyThrows;
import me.alek.enums.Risk;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class HiddenFileCheck extends CheckAdapter implements DetectionNode {

    @Override
    public Pair<String, String> processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        return null;
    }

    @SneakyThrows
    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        if (Files.isHidden(file.toPath())) {
            return new Pair<>("", null);
        }
        if (file.isHidden()) {
            return new Pair<>("", null);
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
