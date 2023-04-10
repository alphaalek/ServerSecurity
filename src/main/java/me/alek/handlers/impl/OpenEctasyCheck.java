package me.alek.handlers.impl;

import me.alek.enums.MalwareType;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Path;

public class  OpenEctasyCheck extends CheckAdapter implements MalwareNode {
    @Override
    public MalwareType getType() {
        return MalwareType.OPEN_ECTASY;
    }

    @Override
    public Pair<String, String> processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        return null;
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        if (resolve(rootFolder, "fr/bodyalhoha/ectasy/SpigotAPI.class")) {
            return new Pair<>("", "SpigotAPI.class");
        }
        return null;
    }
}
