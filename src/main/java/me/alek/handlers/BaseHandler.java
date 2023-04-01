package me.alek.handlers;

import me.alek.cache.containers.CacheContainer;
import me.alek.model.CheckResult;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public abstract class BaseHandler extends Handler {

    @Override
    public List<CheckResult> process(File file, Path rootFolder, CacheContainer cache ) {
        return Arrays.asList(processSingle(file, rootFolder, cache));
    }

    public abstract CheckResult processSingle(File file, Path rootFolder, CacheContainer cache);
}
