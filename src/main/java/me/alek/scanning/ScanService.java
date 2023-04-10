package me.alek.scanning;

import lombok.Getter;
import me.alek.AntiMalwarePlugin;
import me.alek.cache.containers.CacheContainer;
import me.alek.cache.containers.HandlerContainer;
import me.alek.handlers.BaseHandler;
import me.alek.handlers.types.ParseHandler;
import me.alek.model.DuplicatedValueMap;
import me.alek.model.PluginProperties;
import me.alek.model.ResultData;
import me.alek.model.result.CheckResult;
import me.alek.model.result.MalwareCheckResult;
import me.alek.utils.ZipUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScanService {

    private volatile ArrayList<File> files;
    private volatile DuplicatedValueMap<ResultData, Integer> resultMap;
    @Getter
    private volatile ArrayList<File> notDoneFiles;

    private final Scanner scanner;
    private final HandlerContainer handlerContainer;
    private final CacheContainer cacheContainer;

    public ScanService(ArrayList<File> files,
                       DuplicatedValueMap<ResultData, Integer> resultMap,
                       Scanner scanner,
                       HandlerContainer handlerContainer,
                       CacheContainer cacheContainer) {
        this.files = files;
        notDoneFiles = new ArrayList<>();
        notDoneFiles.addAll(files);

        this.resultMap = resultMap;
        this.scanner = scanner;
        this.handlerContainer = handlerContainer;
        this.cacheContainer = cacheContainer;
    }

    public synchronized void updateNotDoneFiles(File file) {
        notDoneFiles.remove(file);
    }

    public synchronized boolean hasMore() {
        return !files.isEmpty();
    }

    private synchronized File get() {
        return files.get(0);
    }

    public synchronized ScanStatus start() {
        if (!hasMore()) return null;
        File file = get();
        files.remove(file);
        return new ScanStatus(file);
    }

    public void execute(ScanStatus status) {
        new BukkitRunnable() {
            @Override
            public void run() {
                asyncExecute(status);
            }
        }.runTaskAsynchronously(AntiMalwarePlugin.getInstance());
    }

    private void asyncExecute(ScanStatus status) {
        if (status == null) return;
        File file = status.getFile();
        try (FileSystem fs = ZipUtils.fileSystemForZip(file.toPath())) {

            if (fs == null) return;
            PluginProperties pluginProperties = new PluginProperties(file);

            boolean hasFileMalware = false;
            List<CheckResult> results = new ArrayList<>();
            for (BaseHandler handler : handlerContainer.getList()) {

                if (handler instanceof ParseHandler) {
                    ((ParseHandler) handler).parse();
                }
                Iterator<Path> rootFolderIterator = fs.getRootDirectories().iterator();
                if (!rootFolderIterator.hasNext()) return;
                Path rootFolder = rootFolderIterator.next();

                CheckResult result = handler.processSingle(file, rootFolder, cacheContainer, pluginProperties);
                if (result instanceof MalwareCheckResult) {
                    if (!hasFileMalware) {
                        scanner.setTotalFilesMalware(scanner.getTotalFilesMalware() + 1);
                        hasFileMalware = true;
                    }
                }
                if (result == null) continue;
                results.add(result);
            }
            status.setState(ScanStatus.State.DONE);
            resultMap.put(new ResultData(results, file, getResultLevel(results)), getResultLevel(results));
            cacheContainer.clearCache(file.toPath());
        } catch (IOException e) {
        }
        updateNotDoneFiles(file);
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
