package me.alek.handlers.impl;

import me.alek.enums.MalwareType;
import me.alek.handlers.types.BytecodeIdentifierHandler;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;

import java.io.File;
import java.nio.file.Path;

public class BukloitCheck extends BytecodeIdentifierHandler implements MalwareNode {

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        if (resolve(rootFolder, "bukloit/Bukloit.class")) {
            return new Pair<>("", null);
        }
        return null;
    }
    @Override
    public String getBytecodeClassString() {
        return "ôéórUUêUé§UêUUûUUû§UâU@â?UâEéôéOé?OôhUODíâhUBDíâhOôOEírDOmâhUUEírDUâmâhOôOEírDOmâhUUEírDOmâq*hUâFírDórOêU@ââOââmrEOméUEâ§";
    }

    @Override
    public MalwareType getType() {
        return MalwareType.BUKLOIT;
    }
}
