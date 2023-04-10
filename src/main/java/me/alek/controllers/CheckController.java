package me.alek.controllers;

import me.alek.cache.containers.CacheContainer;
import me.alek.handlers.CheckAdapter;
import me.alek.handlers.types.OnlySourceLibraryHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.model.Pair;
import me.alek.model.result.CheckResult;
import me.alek.model.PluginProperties;
import me.alek.model.result.MalwareCheckResult;
import me.alek.utils.ZipUtils;
import org.bukkit.Bukkit;
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

        Pair<String, String> data;
        if ((data = handler.preProcessJAR(file, rootFolder, pluginProperties)) != null) {
            return validCheck(handler, data.getKey(), data.getValue());
        }

        while (validClassIterator.hasNext()) {
            Path classPath = validClassIterator.next();
            boolean validClassPath = ZipUtils.validClassPath(classPath);

            ClassNode classNode = cache.fetchClass(file.toPath(), classPath);
            data = handler.processFile(classPath, classNode, file, validClassPath);
            if (data != null) {
                if (handler instanceof OnlySourceLibraryHandler) {
                    if (sourceLib != null)  {
                        if (!classPath.toString().contains(sourceLib)) continue;
                    }
                }
                return validCheck(handler, data.getKey(), data.getValue());
            }
        }
        return null;
    }

    public static CheckResult validCheck(CheckAdapter handler, String variant, String className) {
        if (handler instanceof DetectionNode) {
            DetectionNode node = (DetectionNode) handler;
            if (!variant.equals("")) {
                return new CheckResult(node.getType(), node.getRisk(), variant, className);
            }
            return new CheckResult(node.getType(), node.getRisk(), className);
        }
        if (handler instanceof MalwareNode) {
            MalwareNode node = (MalwareNode) handler;
            return new MalwareCheckResult(node.getType(), variant, className);
        }
        return null;
    }


}
