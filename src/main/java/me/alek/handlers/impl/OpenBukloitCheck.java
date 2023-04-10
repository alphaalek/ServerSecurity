package me.alek.handlers.impl;

import me.alek.enums.MalwareType;
import me.alek.handlers.types.BytecodeIdentifierHandler;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;

import java.io.File;
import java.nio.file.Path;

public class OpenBukloitCheck extends BytecodeIdentifierHandler implements MalwareNode {

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }
    @Override
    public String getBytecodeClassString() {
        return "UâéórUêUé§UêUUû§UâOâ?UEâUâOâEuâhU@âéU@UUáéq§U@âU@âéUéq§";
    }

    @Override
    public MalwareType getType() {
        return MalwareType.OPEN_BUKLOIT;
    }
}
