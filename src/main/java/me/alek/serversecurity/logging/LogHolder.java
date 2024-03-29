package me.alek.serversecurity.logging;

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

    private static boolean inUse;

    public static void setup() {
        holder = new Holder();
        inUse = true;

        holder.scanLogger = new Loggers.ScanLogger();
        holder.opLogger = new Loggers.OPLogger();
        holder.securityLogger = new Loggers.SecurityLogger();
    }

    public static boolean isInUse() {
        return inUse;
    }

}
