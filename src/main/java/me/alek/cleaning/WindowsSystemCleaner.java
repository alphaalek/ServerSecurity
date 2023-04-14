package me.alek.cleaning;

import org.bukkit.entity.Player;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.*;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class WindowsSystemCleaner implements SystemCleaner {

    File compromisedFile;

    @Override
    public void clean(Player player) throws IOException {
        if (compromisedFile.canWrite()) {
            Files.deleteIfExists(compromisedFile.toPath());
        } else {
            player.sendMessage("§8[§6AntiMalware§8] §cDit system er stadig smittet! Sørg for, at du har adgang til at skrive i filer: §7"
                    + System.getenv("APPDATA") + File.separator + "Microsoft" + File.separator + "Windows" + File.separator + "Start Menu" + File.separator +
                    "Programs" + File.separator + "Startup");
        }
    }

    @Override
    public boolean isInfected() throws IOException {
        Process process = Runtime.getRuntime().exec("wmic startup get caption,command");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("javaw")) {
                return isCheckedCompromised();
            }
        }
        return false;
    }

    private boolean isCheckedCompromised() throws IOException {
        File directory = new File(System.getenv("APPDATA") + File.separator + "Microsoft" + File.separator + "Windows"
                + File.separator + "Start Menu" + File.separator + "Programs" + File.separator + "Startup");
        List<File> files = SkyrageJarCleaner.findAllJars(directory);
        for (File file : files) {
            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<? extends ZipEntry> entries = jarFile.entries();
                String eventuallyMainClass = Optional.ofNullable(jarFile.getManifest()).map(m -> m.getMainAttributes().getValue("Main-Class")).map(s -> s.replace(".", "/")).orElse(null);

                while (entries.hasMoreElements()) {

                    ZipEntry zipEntry = entries.nextElement();
                    zipEntry.setCompressedSize(-1L);

                    if (!zipEntry.getName().endsWith(".class")) continue;
                    try (InputStream in = jarFile.getInputStream(zipEntry)) {

                        ClassReader classReader = new ClassReader(in);
                        ClassNode classNode = new ClassNode();
                        classReader.accept(classNode, 0);

                        if (classNode.name == null) continue;
                        if (eventuallyMainClass == null) continue;
                        if (!((classNode.name).equals(eventuallyMainClass))) continue;

                        for (FieldNode fieldNode : classNode.fields) {
                            if (!fieldNode.value.toString().contains("skyrage.de")) continue;
                            compromisedFile = file;
                            return true;
                        }
                    }

                }
                return false;
            }
        }
        return false;
    }
}
