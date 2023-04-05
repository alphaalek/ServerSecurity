package me.alek.scanning;

import lombok.Getter;
import lombok.Setter;
import me.alek.AntiMalwarePlugin;
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
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scanner {

    @Getter
    private final ArrayList<File> files;

    @Getter
    private final ArrayList<File> queriedFiles = new ArrayList<>();

    @Getter
    private final List<ResultData> resultData = new ArrayList<>();
    @Getter @Setter
    private int totalFilesMalware = 0;

    private boolean scanning = false;

    public Scanner(ArrayList<File> files) {
        this.files = files;
        queriedFiles.addAll(files);
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

    public void startScan() {

        if (ScanManager.hasScannersRunning()) {
            return;
        }
        ScanManager.registerScanner(this);
        scanning = true;

        HandlerContainer handlerContainer = new HandlerContainer();
        CacheContainer cache = new CacheContainer();
        DuplicatedValueMap<ResultData, Integer> resultMap = new DuplicatedValueMap<>();

        ScanService service = new ScanService(queriedFiles, resultMap, this, handlerContainer, cache);
        ExecutorService executorService = Executors.newFixedThreadPool(1); // fuck this shit
        for (int i = 0; i <= 2; i++) {
            executorService.execute(new ScanRunnable(service));
        }
        Scanner scanner = this;
        BukkitRunnable waitingRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!service.hasMore()) {
                    List<Map.Entry<ResultData, Integer>> pulledEntries = resultMap.getPulledEntries();
                    pulledEntries.sort(Map.Entry.comparingByValue());
                    pulledEntries.forEach(entry -> resultData.add(entry.getKey()));
                    ScanManager.unregisterScanner(scanner);
                    scanning = false;
                    this.cancel();
                }
            }
        };
        waitingRunnable.runTaskTimer(AntiMalwarePlugin.getInstance(), 0L, 1L);
    }
}
