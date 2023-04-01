package me.alek.cache.registery;

import me.alek.cache.Container;
import me.alek.cache.Registery;

import java.util.Arrays;
import java.util.List;

public class AcceptedPluginsForceOPRegistery extends Registery<String> {

    public AcceptedPluginsForceOPRegistery(Container<String> container) {
        super(container);
    }

    @Override
    public List<String> getElements() {
        return Arrays.asList(
                "Skript",
                "Citizens",
                "Essentials",
                "WorldGuard",
                "skUtilities",
                "SkEssentials"
        );
    }
}
