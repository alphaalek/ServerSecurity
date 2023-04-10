package me.alek.cleanskyrage;

import java.util.function.Supplier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperatingSystem {

    WINDOWS("Windows", WindowsSystemCleaner::new),
    LINUX("Linux", LinuxSystemCleaner::new),
    UNKNOWN("Unknown", null);

    private final String name;
    private final Supplier<SystemCleaner> cleanerFactory;

    public SystemCleaner getCleaner() {
        return cleanerFactory.get();
    }

    public static OperatingSystem getSystem() {
        String system = System.getProperty("os.name").toLowerCase();
        if (system.contains("win")) return WINDOWS;
        else if (system.contains("linux")) return LINUX;
        else return UNKNOWN;
    }
}
