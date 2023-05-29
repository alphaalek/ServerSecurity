package me.alek.logging;

import lombok.Getter;
import me.alek.security.SecurityOptions;

public class LogHolder {

    public static class Holder {
        private Loggers.ScanLogger scanLogger = null;
        private Loggers.OPLogger opLogger = null;
        private Loggers.SecurityLogger securityLogger = null;
    }

    private static Holder holder;

    public static Loggers.ScanLogger getScanLogger() {
        return holder.scanLogger;
    }

    public static Loggers.OPLogger getOPLogger() {
        return holder.opLogger;
    }

    public static Loggers.SecurityLogger getSecurityLogger() {
        return holder.securityLogger;
    }

    public static void setup(SecurityOptions options) {
        holder = new Holder();
        holder.scanLogger = new Loggers.ScanLogger();

        if (options == null) return;
        if (!options.isEnabled()) return;

        if (holder.opLogger == null) {
            holder.opLogger = new Loggers.OPLogger();
        }
        if (holder.securityLogger == null) {
            holder.securityLogger = new Loggers.SecurityLogger();
        }
    }

}
