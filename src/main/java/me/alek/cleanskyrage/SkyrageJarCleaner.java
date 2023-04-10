package me.alek.cleanskyrage;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@UtilityClass
public class SkyrageJarCleaner {

    public List<File> findInfectedJars(File dir) throws IOException {
        return findAllJarsStream(dir).filter(SkyrageJarCleaner::isFileInfected).collect(Collectors.toList());
    }

    public List<File> findAllJars(File dir) throws IOException {
        return findAllJarsStream(dir).collect(Collectors.toList());
    }

    public Stream<File> findAllJarsStream(File dir) throws IOException {
        return Files.walk(dir.toPath()).map(Path::toFile).filter(file -> file.getName().endsWith(".jar"));
    }

    @SneakyThrows
    private boolean isFileInfected(File file) {
        try (ZipFile zip = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                zipEntry.setCompressedSize(-1L);
                if (zipEntry.getName().endsWith("plugin-config.bin")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cleanJar(Player player, File file) throws IOException {
        File infectedFile = new File(file.getParentFile(), file.getName().replace(".jar", "-infected.jar"));
        if (!infectedFile.exists()) {
            player.sendMessage("§8[§6AntiMalware§8] §cFilen bliver brugt af en anden proces og kan derfor ikke tilgås. Geninstaller selv pluginnet!");
            return false;
        }
        File cleanFile = new File(file.getParentFile(), file.getName().replace(".jar", "-clean.jar"));
        JarFile infectedJarFile = new JarFile(infectedFile);
        JarOutputStream out = new JarOutputStream(new FileOutputStream(cleanFile));
        Enumeration<? extends ZipEntry> entries = infectedJarFile.entries();

        String eventuallyMainClass = Optional.ofNullable(infectedJarFile.getManifest()).map(m -> m.getMainAttributes().getValue("Main-Class")).map(s -> s.replace(".", "/")).orElse(null);

        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            zipEntry.setCompressedSize(-1L);

            if (zipEntry.getName().endsWith(".class")) {
                try (InputStream in = infectedJarFile.getInputStream(zipEntry)) {
                    ClassReader classReader = new ClassReader(in);
                    ClassNode classNode = new ClassNode();
                    classReader.accept(classNode, 0);
                    if (classNode.superName == null) continue;
                    if ((classNode.name).equals("Updater")) continue;
                    if ((eventuallyMainClass != null && classNode.name.equals(eventuallyMainClass))
                            || (classNode.superName.equals("org/bukkit/plugin/java/JavaPlugin")
                            || classNode.superName.equals("net/md_5/bungee/api/plugin/Plugin") || classNode.interfaces.stream().anyMatch(s -> s.contains("ClientModInitializer"))
                            || classNode.interfaces.stream().anyMatch(s -> s.contains("ModInitializer")))) {
                        removeVirusFromClass(classNode);
                    }
                    ClassWriter cw = new ClassWriter(1);
                    classNode.accept(cw);
                    ZipEntry newEntry = new ZipEntry(zipEntry.getName());
                    out.putNextEntry(newEntry);
                    writeToFile(out, new ByteArrayInputStream(cw.toByteArray()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (zipEntry.getName().equals("plugin-config.bin")) continue;
            out.putNextEntry(zipEntry);
            writeToFile(out, infectedJarFile.getInputStream(zipEntry));
        }
        infectedJarFile.close();
        out.close();

        infectedFile.delete();
        cleanFile.renameTo(file);
        return true;
    }

    private void removeVirusFromClass(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            for (AbstractInsnNode insnNode : methodNode.instructions) {
                if (!(insnNode instanceof MethodInsnNode)) continue;
                MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
                if (!isInstruction(methodInsnNode)) continue;
                if (!(methodInsnNode.owner + "." + methodInsnNode.name).equalsIgnoreCase("Updater.init")) continue;
                methodNode.instructions.remove(insnNode);
            }
        }
    }

    private void writeToFile(ZipOutputStream outputStream, InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4096];
        try {
            while (inputStream.available() > 0) {
                int data = inputStream.read(buffer);
                outputStream.write(buffer, 0, data);
            }
        } finally {
            inputStream.close();
            outputStream.closeEntry();
        }
    }

    public boolean isInstruction(AbstractInsnNode node) {
        return (!(node instanceof org.objectweb.asm.tree.LineNumberNode) && !(node instanceof org.objectweb.asm.tree.FrameNode) && !(node instanceof org.objectweb.asm.tree.LabelNode));
    }
}
