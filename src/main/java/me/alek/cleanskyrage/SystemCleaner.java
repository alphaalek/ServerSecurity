package me.alek.cleanskyrage;

import org.bukkit.entity.Player;

import java.io.IOException;

public interface SystemCleaner {
    boolean isInfected() throws IOException;
    void clean(Player player) throws IOException;
}
