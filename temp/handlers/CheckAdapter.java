package me.alek.handlers;

import me.alek.cache.containers.CacheContainer;
import me.alek.model.CheckResult;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class CheckAdapter extends BaseHandler {


    public boolean resolve(Path rootFolder, String path) {
        return Files.exists(rootFolder.resolve(path));
    }

    @Override
    public CheckResult processSingle(File file, Path rootFolder, CacheContainer cache) {
        return Check.check(this, file, rootFolder, cache);
    }


    public abstract String processFile(Path classPath, ClassNode classNode, File file, boolean isClass);

    public abstract String preProcessJAR(File file, Path rootFolder);

}
