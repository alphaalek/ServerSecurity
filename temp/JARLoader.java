package me.alek;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JARLoader {

    public static List<File> findAllJars(File dir) {
        if (dir.listFiles() == null) return null;
        List<File> jars = new ArrayList<>();
        Arrays.stream(dir.listFiles())
                .filter(File::isDirectory)
                .forEach(sub -> jars.addAll(findAllJars(sub)));
        return findAllJarsStream(dir).collect(Collectors.toList());
    }

    public static Stream<File> findAllJarsStream(File dir) {
        try {
            return Files.walk(dir.toPath())
                    .map(Path::toFile)
                    .filter(file -> file.getName().endsWith(".jar") && isPlugin(file) && !file.getName().contains("AntiMalware"));
        } catch (IOException ignored) {
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
