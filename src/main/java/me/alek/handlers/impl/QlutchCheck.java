package me.alek.handlers.impl;

import me.alek.enums.MalwareType;
import me.alek.handlers.types.EncryptedKeyHandler;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.model.PluginProperties;

import java.io.File;
import java.nio.file.Path;

public class QlutchCheck extends EncryptedKeyHandler implements MalwareNode {


    @Override
    public String preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        if (file.getName().contains("VaultLib")) {
            return "";
        }
        return null;
    }


    @Override
    public MalwareType getType() {
        return MalwareType.QLUTCH;
    }

    @Override
    public String[] getURLKeys() {
        return new String[]{"https://api.minecraftforceop.com/download.php?port=", "https://api.minecraftforceop.com/name.php"};
    }
}
