package me.alek.serversecurity.utils;

import me.alek.serversecurity.model.PluginProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JARFinder {

    public static ArrayList<File> findAllJars(File dir) {
        if (dir.listFiles() == null) return null;
        final ArrayList<String> absolutePaths = new ArrayList<>();
        return findAllJarsDir(dir, absolutePaths);
    }

    public static ArrayList<File> findAllJarsDir(File dir, List<String> absolutePaths) {
        if (!dir.isDirectory() || dir.listFiles() == null) return new ArrayList<>();

        final ArrayList<File> jars = new ArrayList<>();

        Arrays.stream(dir.listFiles())
                .filter(file -> !absolutePaths.contains(file.getAbsolutePath()))
                .filter(file -> {
                    String pluginName = new PluginProperties(file).getPluginName();
                    return file.getName().endsWith(".jar")
                            && (isPlugin(file) || file.getName().equals("bungee.jar"))
                            && pluginName != null && !(pluginName.equals("ServerSecurity") || pluginName.equals("AntiMalware"));
                })
                .forEach(file -> {
                    absolutePaths.add(file.getAbsolutePath());
                    jars.add(file);
                });
        return jars;
    }

    public static File findJar(File dir, String check) {
        if (dir == null || check == null || dir.listFiles() == null) return null;

        for (File file : dir.listFiles()) {
            if (!file.getName().toLowerCase().startsWith(check.toLowerCase())) continue;
            if (!isPlugin(file)) continue;

            return file;
        }
        return null;
    }

    private static boolean isPlugin(File file) {
        try {
            ZipEntry zipEntry;
            try (ZipFile zipFile = new ZipFile(file)) {
                zipEntry = zipFile.getEntry("plugin.yml");
            }
            return zipEntry != null;
        } catch (IOException ex) {
            return false;
        }
    }
}
