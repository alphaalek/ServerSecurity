package me.alek.handlers.types;

import lombok.Getter;
import me.alek.cache.containers.AcceptedNameObfContainer;
import me.alek.cache.containers.CacheContainer;
import me.alek.cache.containers.ChecksumLibrariesContainer;
import me.alek.cache.containers.ObfuscationContainer;
import me.alek.enums.Risk;
import me.alek.handlers.BaseHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.result.CheckResult;
import me.alek.model.DuplicatedValueMap;
import me.alek.model.FeatureResponse;
import me.alek.model.PluginProperties;
import me.alek.obfuscation.handlers.AbstractObfHandler;
import me.alek.utils.Utils;
import me.alek.utils.ZipUtils;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObfuscationHandler extends BaseHandler implements DetectionNode {

    @Getter
    private static AcceptedNameObfContainer acceptedNameObfContainer;

    @Override
    public CheckResult processSingle(File file, Path rootFolder, CacheContainer cache, PluginProperties pluginProperties) {

        String check = file.getName().toLowerCase();
        if (check.contains("litebans")) return null;
        if (check.contains("featherboard")) return null;

        ObfuscationContainer obfuscationContainer = new ObfuscationContainer();
        ChecksumLibrariesContainer checksumLibrariesContainer = new ChecksumLibrariesContainer();
        acceptedNameObfContainer = new AcceptedNameObfContainer();

        try {
            List<Path> libraries = Files.list(rootFolder).collect(Collectors.toList());

            DuplicatedValueMap<Double, String> libraryPercentages = new DuplicatedValueMap<>();

            for (Path library : libraries) {

                if (!Files.isDirectory(library, LinkOption.NOFOLLOW_LINKS)) continue;
                if (library.getFileName().toString().equals("META-INF")) continue;

                Stream<Path> validClasses = ZipUtils.walkThroughFiles(library);
                Iterator<Path> validClassIterator = validClasses.iterator();

                double obfuscationLibraryCount = 0;
                double totalLibraryCount = 0;

                while (validClassIterator.hasNext()) {

                    Path classPath = validClassIterator.next();
                    if (checksumLibrariesContainer.check(classPath.toString())) continue;
                    if (!ZipUtils.validClassPath(classPath)) continue;

                    ClassNode classNode = cache.fetchClass(file.toPath(), classPath);
                    if (classNode == null) continue;

                    totalLibraryCount++;
                    int obfuscationClassCount = 0;

                    for (AbstractObfHandler feature : obfuscationContainer.getList()) {

                        for (Map.Entry<String, FeatureResponse> responseEntry : feature.check(classNode).entrySet()) {

                            FeatureResponse featureResponseModel = responseEntry.getValue();

                            if (!featureResponseModel.getFeedback()) continue;
                            if (!featureResponseModel.isRelevant()) continue;

                            Risk featureRisk = featureResponseModel.getFeatureRisk();
                            obfuscationClassCount += featureRisk.getObfuscationLevel();
                        }
                    }
                    if (obfuscationClassCount >= 2) {
                        obfuscationLibraryCount++;
                    }
                }
                double averagePercentage = Utils.arithmeticSecure(obfuscationLibraryCount, totalLibraryCount);
                if (!Double.isNaN(averagePercentage))
                    libraryPercentages.put(averagePercentage, library.getFileName().toString());
            }

            if (libraryPercentages.getPulledEntries().isEmpty()) return null;
            Map.Entry<Double, String> maxEntry = Collections.max(libraryPercentages.getPulledEntries(), Map.Entry.comparingByKey());
            if (maxEntry.getKey() > 0.55) {
                return new CheckResult("Obfuscated (" + Utils.percentage(maxEntry.getKey()) + ")", Risk.FAKE_CRITICAL, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getType() {
        return "Obfuscation";
    }

    @Override
    public Risk getRisk() {
        return Risk.FAKE_CRITICAL;
    }
}
