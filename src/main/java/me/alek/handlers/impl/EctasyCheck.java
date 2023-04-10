package me.alek.handlers.impl;

import me.alek.enums.MalwareType;
import me.alek.handlers.types.EncryptedKeyHandler;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;

import java.io.File;
import java.nio.file.Path;

public class EctasyCheck extends EncryptedKeyHandler implements MalwareNode {

    @Override
    public MalwareType getType() {
        return MalwareType.ECTASY;
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        if (file.getName().endsWith("bungee.jar") && file.getAbsolutePath().contains("PluginMetrics")) {
            return new Pair<>("Backdoor", null);
        } else if (resolve(rootFolder, "skidder/get/lolled/")) {
            return new Pair<>("Backdoor", null);
        }
        return null;
    }

    @Override
    public String[] getURLKeys() {
        return new String[]{"https://bodyalhoha.com/bungee.jar", "http://javax.xml.transform.sax.SAXSource/feature"};
    }

}

