package me.alek.utils;

public final class Handshake {

    private Runnable request;

    public Handshake() {}

    public Handshake(Runnable request) {
        this.request = request;
    }

    public void setRequest(Runnable request) {
        this.request = request;
    }

    public void onResponse() {
        if (request == null) return;
        request.run();
    }
}
