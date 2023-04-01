package me.alek.handlers;

import me.alek.cache.containers.CacheContainer;
import me.alek.handlers.types.OnlySourceLibraryHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.model.CheckResult;
import me.alek.utils.ZipUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class Check {

    public static String getSourceLib(File file) {
        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if (entry.getName().contains("plugin.yml")) {
                    Properties properties = new Properties();
                    InputStream inputStream = jarFile.getInputStream(entry);
                    properties.load(inputStream);

                    String mainClassPath = properties.getProperty("main");
                    if (mainClassPath  == null) continue;

                    mainClassPath = mainClassPath.replaceAll("\\.", "/");
                    return StringUtils.substringBeforeLast(mainClassPath, "/") + "/";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static CheckResult check(CheckAdapter handler, File file, Path rootFolder, CacheContainer cache) {

        Stream<Path> validClasses = ZipUtils.walkThroughFiles(rootFolder);
        Iterator<Path> validClassIterator = validClasses.iterator();
        String sourceLib = getSourceLib(file);

        String variant;
        if ((variant = handler.preProcessJAR(file, rootFolder)) != null) {
            return validCheck(handler, variant);
        }

        while (validClassIterator.hasNext()) {
            Path classPath = validClassIterator.next();
            boolean validClassPath = ZipUtils.validClassPath(classPath);

            ClassNode classNode = cache.fetchClass(file.toPath(), classPath);
            Bukkit.broadcastMessage(classNode + "");
            if ((variant = handler.processFile(classPath, classNode, file, validClassPath)) != null) {
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
