package me.alek.handlers;

import me.alek.cache.containers.CacheContainer;
import me.alek.model.result.CheckResult;
import me.alek.model.PluginProperties;

import java.io.File;
import java.nio.file.Path;

public abstract class BaseHandler{

    public abstract CheckResult processSingle(File file, Path rootFolder, CacheContainer cache, PluginProperties pluginProperties);
}
