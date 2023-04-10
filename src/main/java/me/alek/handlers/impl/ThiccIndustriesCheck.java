package me.alek.handlers.impl;


import lombok.Getter;
import me.alek.enums.MalwareType;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.ParseHandler;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ThiccIndustriesCheck extends CheckAdapter implements MalwareNode, ParseHandler {

    private List<ThiccIndustriesVariant> classes;

    public class ThiccIndustriesVariant {
        @Getter private final String classCheck;
        @Getter private final String variant;
        public ThiccIndustriesVariant(String classCheck, String variant) {
            this.classCheck = classCheck;
            this.variant = variant;
        }
    }

    @Override
    public void parse() {
        if (classes != null) return;
        classes = Arrays.asList(
                new ThiccIndustriesVariant("Injector", "Injector"),
                new ThiccIndustriesVariant("DWeb", "Discord Webhook"),
                new ThiccIndustriesVariant("Debugger", "Handler"),
                new ThiccIndustriesVariant("Config", "Config")
        );
    }
    @Override
    public Pair<String, String> processFile(Path classPath, ClassNode classNode, File file, boolean isClass) {
        return null;
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        for (ThiccIndustriesVariant variant : classes) {
            String className = "com/thiccindustries/debugger/" + variant.classCheck + ".class";
            if (resolve(rootFolder, className)) {
                return new Pair<>(variant.getVariant(), className);
            }
        }
        return null;
    }

    @Override
    public MalwareType getType() {
        return MalwareType.THICCINDUSTRIES;
    }

}
