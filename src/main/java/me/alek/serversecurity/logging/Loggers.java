package me.alek.serversecurity.logging;

public class Loggers {

    public static class SecurityLogger extends AbstractLogger {

        @Override
        protected String getFileName() {
            return "security.log";
        }

        @Override
        protected String getName() {
            return "security";
        }
    }

    public static class ScanLogger extends AbstractLogger {

        @Override
        protected String getFileName() {
            return "scanning.log";
        }

        @Override
        protected String getName() {
            return "scanning";
        }
    }

    public static class OPLogger extends AbstractLogger {

        @Override
        protected String getFileName() {
            return "ops.log";
        }

        @Override
        protected String getName() {
            return "ops";
        }
    }

}
