package me.alek.cache.accepted;

import me.alek.cache.Container;
import me.alek.cache.Registery;

import java.util.Arrays;
import java.util.List;

public class AcceptedPluginsForceOP {

    public static class ForceOPContainer extends Container<String> {

        @Override
        public Registery<String> getRegistery() {
            return new ForceOPRegistry(this);
        }
    }

    public static class ForceOPRegistry extends Registery<String> {

        public ForceOPRegistry(Container<String> container) {
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
                    "SkEssentials",
                    "WildSkript"
            );
        }
    }

}
