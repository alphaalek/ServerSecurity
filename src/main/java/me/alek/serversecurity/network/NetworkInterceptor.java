package me.alek.serversecurity.network;

public interface NetworkInterceptor {

    void enable();

    void disable();

    boolean isEnabled();

    void checkConnect(String host, int port);
}
