package me.alek.logging;

public class Loggers {

    public static class SecurityLogger extends AbstractLogger {

        @Override
        protected String getFileName() {
            return "security.log";
        }
    }

    public static class ScanLogger extends AbstractLogger {

        @Override
        protected String getFileName() {
            return "scanning.log";
        }
    }

    public static class OPLogger extends AbstractLogger {

        @Override
        protected String getFileName() {
            return "ops.log";
        }
    }

}