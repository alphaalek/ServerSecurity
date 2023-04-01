package me.alek.handlers.impl;

import me.alek.enums.MalwareType;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.nodes.MalwareNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Path;

public class  OpenEctasyCheck extends CheckAdapter implements MalwareNode {
    @Override
    public MalwareType getType() {
        return MalwareType.OPEN_ECTASY;
    }

    @Override
    public String processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        return null;
    }

    @Override
    public String preProcessJAR(File file, Path rootFolder) {
        if (resolve(rootFolder, "fr/bodyalhoha/ectasy/SpigotAPI.class")) {
            return "SpigotAPI";
        }
        return null;
    }
}
