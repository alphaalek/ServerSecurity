package me.alek.serversecurity.utils;

public final class Appender {

    private Runnable request;

    public Appender() {}

    public Appender(Runnable request) {
        this.request = request;
    }

    public void setRunnable(Runnable request) {
        this.request = request;
    }

    public void onResponse() {
        if (request == null) return;
        request.run();
    }
}
