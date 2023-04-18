package me.alek.security.event;

import lombok.Getter;
import me.alek.security.SecurityManager;
import org.bukkit.event.Listener;

public abstract class AbstractListener implements Listener {

    @Getter private final SecurityManager manager;

    public AbstractListener(SecurityManager manager) {
        this.manager = manager;
    }
}
