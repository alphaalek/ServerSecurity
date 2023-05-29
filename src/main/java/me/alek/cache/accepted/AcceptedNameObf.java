package me.alek.cache.accepted;

import me.alek.cache.Container;
import me.alek.cache.Registery;

import java.util.Arrays;
import java.util.List;

public class AcceptedNameObf {

    public static class ObfContainer extends Container<String> {

        @Override
        public Registery<String> getRegistery() {
            return new ObfRegistry(this);
        }
    }

    public static class ObfRegistry extends Registery<String> {

        public ObfRegistry(Container<String> container) {
            super(container);
        }

        @Override
        public List<String> getElements() {
            return Arrays.asList(
                    "gui",
                    "tab",
                    "max",
                    "min",
                    "log"
            );
        }
    }

}
