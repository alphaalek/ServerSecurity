package me.alek;

import lombok.Getter;
import me.alek.cache.containers.AcceptedPluginsForceOPContainer;
import me.alek.cache.containers.CacheContainer;
import me.alek.cache.containers.HandlerContainer;
import me.alek.enums.Risk;
import me.alek.handlers.Handler;
import me.alek.handlers.types.ParseHandler;
import me.alek.model.CheckResult;
import me.alek.model.DuplicatedValueMap;
import me.alek.model.PluginProperties;
import me.alek.model.ResultData;
import me.alek.utils.Utils;
import me.alek.utils.ChatUtils;
import me.alek.utils.ZipUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.*;

public class Scanner {

    private final List<File> files;
    private final Player player;
    private final boolean deepScan;
    private int totalFilesRisk = 0;
    private boolean scanning = false;
    @Getter
    private static AcceptedPluginsForceOPContainer acceptedPluginsForceOPContainer;

    public Scanner(Player player, List<File> files, boolean deepScan) {
        this.files = files;
        this.player = player;
        this.deepScan = deepScan;
    }

    public void scan() {

        HandlerContainer handlerContainer = new HandlerContainer();
        player.sendMessage("§8[§6AntiMalware§8] §7Scanner " + files.size() + " filer for malware. Vent venligst...");
        scanning = true;
        CacheContainer cache = new CacheContainer();
        acceptedPluginsForceOPContainer = new AcceptedPluginsForceOPContainer();

        DuplicatedValueMap<ResultData, Integer> resultMap = new DuplicatedValueMap<>();

        for (File file : files) {
            try (FileSystem fs = ZipUtils.fileSystemForZip(file.toPath())) {

                if (fs == null) return;

                PluginProperties pluginProperties = new PluginProperties(file);

                List<CheckResult> results = new ArrayList<>();
                synchronized (this) {
                    for (Handler handler : handlerContainer.getList()) {

                        if (handler instanceof ParseHandler parseHandler) {
                            parseHandler.parse();
                        }

                        Iterator<Path> rootFolderIterator = fs.getRootDirectories().iterator();
                        if (!rootFolderIterator.hasNext()) return;
                        Path rootFolder = rootFolderIterator.next();

                        List<CheckResult> result = handler.process(file, rootFolder, cache, pluginProperties);

                        if (result == null) continue;
                        results.addAll(result);
                    }
                    resultMap.put(new ResultData(results, file, getResultLevel(results)), getResultLevel(results));
                    cache.clearCache(file.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<Map.Entry<ResultData, Integer>> pulledEntries = resultMap.getPulledEntries();
        pulledEntries.sort(Map.Entry.comparingByValue());
        for (Map.Entry<ResultData, Integer> entry : pulledEntries) {
            logResults(entry.getKey(), player);
        }
        player.sendMessage("§8[§6AntiMalware§8] §7Scannede i alt " + files.size() + " filer igennem og fandt " + totalFilesRisk + " filer med risikoer.");
    }

    public int getResultLevel(List<CheckResult> results) {
        int totalLevel = 0;
        for (CheckResult result : results) {
            if (result == null) continue;
            totalLevel += result.getRisk().getDetectionLevel();
        }
        return totalLevel;
    }

    public void logResults(ResultData data, Player player) {

        List<CheckResult> results = data.getResults();
        double level = data.getLevel();

        if (results.stream().filter(Objects::isNull).toList().size() == results.size()) return;

        if (deepScan) {
            player.sendMessage(ChatUtils.getChatSymbol(level) + "§r" + ChatUtils.getChatColor(level) + data.getFile().getName());
        }
        AbstractMap.SimpleEntry<Risk, StringBuilder>[] riskStringBuilders = new AbstractMap.SimpleEntry[5];
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
        if (detected && deepScan) {
            totalFilesRisk++;

            boolean sendTemp = false;
            String temp = "";
            for (AbstractMap.SimpleEntry<Risk, StringBuilder> entry : riskStringBuilders) {

                if (entry.getValue().isEmpty()) continue;

                String message = " §7- " + entry.getKey().getChatColor() + entry.getKey().getName() + ": §7" + entry.getValue().substring(2);
                switch (entry.getKey()) {
                    case FAKE_CRITICAL -> {
                        temp = entry.getValue().toString();
                        continue;
                    }
                    case HIGH -> {
                        message = message + temp;
                        sendTemp = true;
                    }
                    default -> {
                        if (!sendTemp) {
                            if (!temp.equals("")) {
                                player.sendMessage(" §7- §cHøj risiko: §7" + temp.substring(2));
                            }
                        }
                    }
                }
                player.sendMessage(message);
            }
        }
        player.sendMessage(ChatUtils.getMessage(level, deepScan, data.getFile().getName()));
        player.sendMessage("");
    }

}
