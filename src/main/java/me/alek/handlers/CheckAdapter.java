package me.alek.handlers;

import me.alek.cache.containers.CacheContainer;
import me.alek.controllers.CheckController;
import me.alek.model.Pair;
import me.alek.model.result.CheckResult;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class CheckAdapter extends BaseHandler {


    public boolean resolve(Path rootFolder, String path) {
        return Files.exists(rootFolder.resolve(path));
    }

    @Override
    public CheckResult processSingle(File file, Path rootFolder, CacheContainer cache, PluginProperties properties) {
        return CheckController.check(this, file, rootFolder, cache, properties);
    }


    public abstract Pair<String, String> processFile(Path classPath, ClassNode classNode, File file, boolean isClass);

    public abstract Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties);

}
