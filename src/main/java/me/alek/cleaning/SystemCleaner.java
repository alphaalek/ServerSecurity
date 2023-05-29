package me.alek.cleaning;

import org.bukkit.entity.Player;

import java.io.IOException;

public interface SystemCleaner {

    SystemInfectionType getInfection() throws IOException;

    void clean(SystemInfectionType type, Player player) throws IOException;
}
