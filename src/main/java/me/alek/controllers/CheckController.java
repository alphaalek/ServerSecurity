package me.alek.controllers;

import me.alek.cache.containers.CacheContainer;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.OnlySourceLibraryHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.model.CheckResult;
import me.alek.model.PluginProperties;
import me.alek.utils.ZipUtils;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

public class CheckController {

    public static CheckResult check(CheckAdapter handler, File file, Path rootFolder, CacheContainer cache, PluginProperties pluginProperties) {

        Stream<Path> validClasses = ZipUtils.walkThroughFiles(rootFolder);
        Iterator<Path> validClassIterator = validClasses.iterator();

        String sourceLib = null;
        if (pluginProperties != null) {
            sourceLib = pluginProperties.getSourceLib();
        }

        String variant;
        if ((variant = handler.preProcessJAR(file, rootFolder, pluginProperties)) != null) {
            return validCheck(handler, variant);
        }

        while (validClassIterator.hasNext()) {
            Path classPath = validClassIterator.next();
            boolean validClassPath = ZipUtils.validClassPath(classPath);

            ClassNode classNode = cache.fetchClass(file.toPath(), classPath);
            variant = handler.processFile(classPath, classNode, file, validClassPath);
            if (variant != null) {
                if (handler instanceof OnlySourceLibraryHandler) {
                    if (sourceLib != null)  {
                        if (!classPath.toString().contains(sourceLib)) continue;
                    }
                }
                return validCheck(handler, variant);
            }
        }
        return null;
    }

    public static CheckResult validCheck(CheckAdapter handler, String variant) {
        if (handler instanceof DetectionNode node) {
            if (!variant.equals("")) {
                return new CheckResult(node.getType(), node.getRisk(), variant);
            }
            return new CheckResult(node.getType(), node.getRisk());
        }
        if (handler instanceof MalwareNode node) {
            return new CheckResult(node.getType().getName(), true, variant);
        }
        return null;
    }


}
