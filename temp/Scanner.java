package me.alek;

import me.alek.cache.containers.CacheContainer;
import me.alek.cache.containers.HandlerContainer;
import me.alek.enums.Risk;
import me.alek.handlers.impl.detections.SystemAccessCheck;
import me.alek.handlers.types.ParseHandler;
import me.alek.model.CheckResult;
import me.alek.utils.Utils;
import me.alek.utils.ZipUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.*;

public class Scanner {

    private final List<File> files;
    private final Player player;

    private int totalFilesRisk = 0;

    public Scanner(List<File> files, Player player) {
        this.files = files;
        this.player = player;
    }

    public void scan() {

        HandlerContainer handlerContainer = new HandlerContainer();
        player.sendMessage("§8[§6AntiMalware§8] §7Scanner " + files.size() + " filer for malware. Vent venligst...");
        CacheContainer cache = new CacheContainer();

        for (File file : files) {
            try (FileSystem fs = ZipUtils.fileSystemForZip(file.toPath())) {

                if (fs == null) return;

                List<CheckResult> results = new ArrayList<>();
                for (Handler handler : handlerContainer.getList()) {

                    if (handler instanceof ParseHandler parseHandler) {
                        parseHandler.parse();
                    }

                    Iterator<Path> rootFolderIterator = fs.getRootDirectories().iterator();
                    if (!rootFolderIterator.hasNext()) return;
                    Path rootFolder = rootFolderIterator.next();

                    List<CheckResult> result = handler.process(file, rootFolder, cache);

                    if (result == null) continue;
                    results.addAll(result);
                }
                cache.clearCache(file.toPath());
                logResults(file, results);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        player.sendMessage("§8[§6AntiMalware§8] §7Scannede i alt " + files.size() + " filer igennem og fandt " + totalFilesRisk + " filer med risikoer.");
    }

    public void logResults(File file, List<CheckResult> results) {

        if (results.stream().filter(Objects::nonNull).toList().size() == results.size()) return;

        String warning = (results.stream().anyMatch(result -> {
            if (result == null) return false;
            return result.isMalware();
        }) ? "!?! " : "");
        player.sendMessage(warning + file.getName());

        AbstractMap.SimpleEntry<Risk, StringBuilder>[] riskStringBuilders = new AbstractMap.SimpleEntry[4];
        int i = 0;
        for (Risk risk : Risk.values()) {
            riskStringBuilders[i] = new AbstractMap.SimpleEntry<>(risk, new StringBuilder());
            i++;
        }

        boolean detected = false;
        for (CheckResult result : results) {

            if (result == null) continue;
            detected = true;

            Arrays.stream(riskStringBuilders)
                    .filter(entry -> entry.getKey() == result.getRisk())
                    .forEach(entry -> entry.getValue().append(", ").append(Utils.formatCheckResult(result)));
        }
        if (detected) {
            totalFilesRisk++;
            for (AbstractMap.SimpleEntry<Risk, StringBuilder> entry : riskStringBuilders) {

                if (entry.getValue().isEmpty()) continue;

                player.sendMessage(" §7- " + entry.getKey().getChatColor() + entry.getKey().getName() + ": " + entry.getValue().toString().substring(2));
            }
        }
        player.sendMessage("");
    }
}
