package me.alek.handlers.impl;


import lombok.Getter;
import lombok.Setter;
import me.alek.controllers.BytecodeController;
import me.alek.enums.MalwareType;
import me.alek.handlers.types.AbstractInstructionHandler;
import me.alek.handlers.types.ParseHandler;
import me.alek.handlers.types.nodes.MalwareNode;
import me.alek.model.Pair;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NewSkyRageCheck extends AbstractInstructionHandler implements MalwareNode, ParseHandler {

    public NewSkyRageCheck() {
        super(MethodInsnNode.class);
    }

    private Holder holder;

    private static class SkyRageVariant {

        @Getter @Setter private boolean found = false;

        @Getter private final String variant;
        @Getter private final String check;

        private SkyRageVariant(String variant, String check) {
            this.variant = variant;
            this.check = check;
        }
    }

    private static class Holder {

        private final List<SkyRageVariant> variants = Arrays.asList(
                new SkyRageVariant("URL", "aHR0cDovL2xvY2FsaG9zdDozMDAxL3BvbGljeS5qc29u"),
                new SkyRageVariant("Log", "kernel-certs-debug4917.log"),
                new SkyRageVariant("Dgnu", "-Dgnu")
        );
    }

    @Override
    public void parse() {
        holder = new Holder();
    }

    @Override
    public Pair<String, String> preProcessJAR(File file, Path rootFolder, PluginProperties pluginProperties) {
        return null;
    }

    @Override
    public String processAbstractInsn(MethodNode methodNode, AbstractInsnNode abstractInsnNode, Path classPath) {
        String bytesFormatted = BytecodeController.getBytesInvocation(abstractInsnNode);
        if (bytesFormatted != null) {

            for (SkyRageVariant variant : holder.variants) {
                if (variant.isFound()) continue;
                if (variant.getCheck().equals(bytesFormatted)) {
                    variant.setFound(true);
                }
            }
        }
        List<SkyRageVariant> foundVariants = holder.variants.stream().filter(SkyRageVariant::isFound).collect(Collectors.toList());
        if (foundVariants.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (SkyRageVariant variant : foundVariants) {
                builder.append(", ").append(variant.getVariant());
            }
            return builder.substring(2);
        }

        return null;
    }

    @Override
    public MalwareType getType() {
        return MalwareType.SKYRAGE;
    }
}
