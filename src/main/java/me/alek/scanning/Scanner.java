package me.alek.scanning;

import lombok.Getter;
import me.alek.cache.containers.CacheContainer;
import me.alek.cache.containers.HandlerContainer;
import me.alek.handlers.BaseHandler;
import me.alek.handlers.types.ParseHandler;
import me.alek.model.result.CheckResult;
import me.alek.model.DuplicatedValueMap;
import me.alek.model.PluginProperties;
import me.alek.model.ResultData;
import me.alek.model.result.MalwareCheckResult;
import me.alek.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Scanner {

    @Getter
    private final List<File> files;
    @Getter
    private final List<ResultData> resultData = new ArrayList<>();
    @Getter
    private int totalFilesMalware = 0;

    private boolean scanning = false;

    public Scanner(List<File> files) {
        this.files = files;
    }

    public boolean isScanning() {
        return scanning;
    }

    public boolean hasMalware() {
        return totalFilesMalware != 0;
    }
    public enum ScanResponse {
        ERROR, SCANNERS_RUNNING, SUCCESS
    }

    public ScanResponse startScan() {

        if (ScanManager.hasScannersRunning()) {
            return ScanResponse.SCANNERS_RUNNING;
        }
        ScanManager.registerScanner(this);
        scanning = true;

        HandlerContainer handlerContainer = new HandlerContainer();
        CacheContainer cache = new CacheContainer();

        DuplicatedValueMap<ResultData, Integer> resultMap = new DuplicatedValueMap<>();

        for (File file : files) {
            try (FileSystem fs = ZipUtils.fileSystemForZip(file.toPath())) {

                if (fs == null) return ScanResponse.SCANNERS_RUNNING;
                PluginProperties pluginProperties = new PluginProperties(file);

                boolean hasFileMalware = false;
                List<CheckResult> results = new ArrayList<>();
                for (BaseHandler handler : handlerContainer.getList()) {

                    if (handler instanceof ParseHandler) {
                        ((ParseHandler)handler).parse();
                    }
                    Iterator<Path> rootFolderIterator = fs.getRootDirectories().iterator();
                    if (!rootFolderIterator.hasNext()) return ScanResponse.SCANNERS_RUNNING;
                    Path rootFolder = rootFolderIterator.next();

                    CheckResult result = handler.processSingle(file, rootFolder, cache, pluginProperties);
                    if (result instanceof MalwareCheckResult) {
                        if (!hasFileMalware) {
                            totalFilesMalware++;
                            hasFileMalware = true;
                        }
                    }
                    if (result == null) continue;
                    results.add(result);
                }
                resultMap.put(new ResultData(results, file, getResultLevel(results)), getResultLevel(results));
                cache.clearCache(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<Map.Entry<ResultData, Integer>> pulledEntries = resultMap.getPulledEntries();
        pulledEntries.sort(Map.Entry.comparingByValue());
        pulledEntries.forEach(entry -> resultData.add(entry.getKey()));
        ScanManager.unregisterScanner(this);
        scanning = false;
        return ScanResponse.SUCCESS;
    }

    public int getResultLevel(List<CheckResult> results) {
        int totalLevel = 0;
        for (CheckResult result : results) {
            if (result == null) continue;
            totalLevel += result.getRisk().getDetectionLevel();
        }
        return totalLevel;
    }

}
