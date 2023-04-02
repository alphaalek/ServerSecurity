package me.alek.handlers;

import me.alek.cache.containers.CacheContainer;
import me.alek.model.CheckResult;
import me.alek.model.PluginProperties;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public abstract class Handler {

    public abstract List<CheckResult> process(File file, Path rootFolder, CacheContainer cache, PluginProperties pluginProperties);
}
