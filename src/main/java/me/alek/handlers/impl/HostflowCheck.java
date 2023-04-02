package me.alek.handlers.impl;

import me.alek.enums.MalwareType;
import me.alek.handlers.types.EncryptedKeyHandler;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.model.PluginProperties;

import java.io.File;
import java.nio.file.Path;

public class HostflowCheck extends EncryptedKeyHandler implements MalwareNode {

    @Override
    public MalwareType getType() {
        return MalwareType.HOSTFLOW;
    }


    @Override
    public String preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        /*if (resolve(rootFolder, "javassist/PingMessage.class")) {
            return "";
        } else if (resolve(rootFolder,"javaassist/ResponseContainer.class")) {
            return "";
        }*/
        return null;
    }

    @Override
    public String[] getURLKeys() {
        return new String[]{"http://client.hostflow.eu:5050/ws"};
    }
}
