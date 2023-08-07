package me.alek.serversecurity.utils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.stream.Stream;

public class ZipUtils {

    public static Stream<Path> walkThroughFiles(Path dir) {
        return walkThroughRecursiveLimitedFiles(dir, 0);
    }

    public static Stream<Path> walkThroughRecursiveLimitedFiles(Path dir, int index) {
        if (index == 10) {
            return Stream.of();
        }
        int recursive = index + 1;
        if (dir.getFileName() != null && dir.getFileName().toString().equals(".")) {
            return Stream.of();
        } else if (Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS)) {
            try {
                return Files.list(dir).flatMap(path -> walkThroughRecursiveLimitedFiles(path, recursive));
            } catch (IOException e) {
                return Stream.of();
            }
        } else if (Files.isSymbolicLink(dir)) {
            return Stream.of();
        } else {
            return Stream.of(dir);
        }
    }

    public static FileSystem fileSystemForZip(final Path pathToZip) {
        /*try {
            return FileSystems.newFileSystem(pathToZip, new HashMap<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return FileSystems.getFileSystem(URI.create("jar:" + pathToZip.toUri() + "!/"));
        } catch (Exception e2) {
            e2.printStackTrace();
        }*/
        try {
            return FileSystems.newFileSystem(URI.create("jar:" + pathToZip.toUri() + "!/"), new HashMap<>());
        } catch (IOException e3) {
            return null;
        }
    }

    public static boolean validClassPath(Path path) {
        return path.toString().endsWith(".class") && !path.toString().contains("__MACOSX");
    }

    public static String getRootClass(String classPath) {
        String[] classNameSplitted = classPath.split("/");
        return classNameSplitted[classNameSplitted.length - 1];
    }
}
