package me.alek.serversecurity.network;

public final class SneakyThrow {

    public static void sneakyThrow(final Throwable t) throws Throwable {
        throw t;
    }

}
