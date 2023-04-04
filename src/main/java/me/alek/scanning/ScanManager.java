package me.alek.scanning;

import me.alek.scanning.Scanner;

import java.util.ArrayList;

public class ScanManager {

    private static Scanner latestScanner = null;
    private static final ArrayList<Scanner> scannersRunning = new ArrayList<>();

    public static void registerScanner(Scanner scanner) {
        if (scannersRunning.contains(scanner)) return;
        latestScanner = scanner;
        scannersRunning.add(scanner);
    }

    public static void unregisterScanner(Scanner scanner) {
        if (!scannersRunning.contains(scanner)) return;
        scannersRunning.remove(scanner);
    }

    public static boolean hasScannersRunning() {
        return !scannersRunning.isEmpty();
    }

    public static Scanner getLatestScanner() {
        return latestScanner;
    }
}
