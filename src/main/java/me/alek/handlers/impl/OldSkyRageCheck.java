package me.alek.handlers.impl;

import me.alek.cache.malware.SkyrageLibraries;
import me.alek.enums.MalwareType;
import me.alek.handlers.types.EncryptedKeyHandler;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.handlers.types.ParseHandler;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;

import java.io.File;
import java.nio.file.Path;

public class OldSkyRageCheck extends EncryptedKeyHandler implements ParseHandler, MalwareNode {

    private SkyrageLibraries.LibrariesContainer container;

    @Override
    public void parse() {
        if (container != null) return;
        container = new SkyrageLibraries.LibrariesContainer();
    }

    @Override
    public MalwareType getType() {
        return MalwareType.SKYRAGE;
    }

    @Override
    public String[] getURLKeys() {
        return new String[]{"http://files.skyrage.de/update", "http://files.skyrage.de/mvd", "http://files.ckyroor.com"};
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        if (resolve(rootFolder, "plugin-config.bin")) {
            return new Pair<>("config-bin", null);
        }
        for (String metaInfString : container.getList()) {
            if (resolve(rootFolder, metaInfString)) {
                return new Pair<>("META-INF library", null);
            }
        }
        return null;
    }

}
