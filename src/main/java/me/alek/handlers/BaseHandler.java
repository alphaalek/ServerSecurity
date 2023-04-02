package me.alek.handlers;

import me.alek.cache.containers.CacheContainer;
import me.alek.model.CheckResult;
import me.alek.model.PluginProperties;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public abstract class BaseHandler extends Handler {

    @Override
    public List<CheckResult> process(File file, Path rootFolder, CacheContainer cache, PluginProperties pluginProperties) {
        return Arrays.asList(processSingle(file, rootFolder, cache, pluginProperties));
    }

    public abstract CheckResult processSingle(File file, Path rootFolder, CacheContainer cache, PluginProperties pluginProperties);
}
