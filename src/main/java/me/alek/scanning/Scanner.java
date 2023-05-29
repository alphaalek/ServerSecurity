package me.alek.scanning;

import lombok.Getter;
import lombok.Setter;
import me.alek.AntiMalwarePlugin;
import me.alek.cache.CacheContainer;
import me.alek.cache.malware.Handlers;
import me.alek.logging.LogHolder;
import me.alek.model.DuplicatedValueMap;
import me.alek.model.ResultData;
import me.alek.utils.Handshake;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class Scanner {

    @Getter private final ArrayList<File> files;

    @Getter private ScanService service;
    @Getter private final ArrayList<File> queriedFiles = new ArrayList<>();
    @Getter private final List<ResultData> resultData = new ArrayList<>();

    @Getter private boolean scanning = false;
    @Getter private Instant start;
    @Getter @Setter private int totalFilesMalware = 0;

    public Scanner(ArrayList<File> files) {
        this.files = files;
        queriedFiles.addAll(files);
    }

    public boolean hasMalware() {
        return totalFilesMalware != 0;
    }

    public void startScan() {
        startScan(null);
    }

    public void startScan(Handshake whenDone) {
        if (ScanHandler.hasScannersRunning()) {
            return;
        }
        ScanHandler.registerScanner(this);
        scanning = true;
        start = Instant.now();

        final Handlers.HandlerContainer handlerContainer = new Handlers.HandlerContainer();
        final CacheContainer cache = new CacheContainer();
        final DuplicatedValueMap<ResultData, Integer> resultMap = new DuplicatedValueMap<>();

        service = new ScanService(queriedFiles, resultMap, this, handlerContainer, cache);

        /*ExecutorService executorService = Executors.newFixedThreadPool(files.size());
        for (int i = 0; i <= files.size()-1; i++) {
            executorService.execute(new ScanRunnable(service));
        }*/
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i = 0; i <= 1; i++) {
            executorService.execute(new ScanRunnable(service));
        }

        final Scanner scanner = this;
        final BukkitRunnable waitingRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!service.hasMore()) {
                    final List<Map.Entry<ResultData, Integer>> pulledEntries = resultMap.getPulledEntries();
                    LogHolder.getScanLogger().log(Level.INFO,"Scannede " + files.size() + " filer igennem og fandt " + totalFilesMalware
                            + " med virus. (" + Duration.between(start, Instant.now()).toMillis() + "ms)");

                    pulledEntries.sort(Map.Entry.comparingByValue());
                    pulledEntries.forEach(entry -> resultData.add(entry.getKey()));

                    ScanHandler.unregisterScanner(scanner);
                    scanning = false;
                    if (whenDone != null) whenDone.onResponse();

                    this.cancel();
                }
            }
        };
        waitingRunnable.runTaskTimer(AntiMalwarePlugin.getInstance(), 0L, 40L);
    }
}
