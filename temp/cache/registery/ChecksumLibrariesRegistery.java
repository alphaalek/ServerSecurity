package me.alek.cache.registery;

import me.alek.cache.Container;
import me.alek.cache.Registery;

import java.util.Arrays;
import java.util.List;

public class ChecksumLibrariesRegistery extends Registery<String> {

    public ChecksumLibrariesRegistery(Container<String> container) {
        super(container);
    }

    @Override
    public List<String> getElements() {
        return Arrays.asList(
                "/net/minecraft/server/",
                "/us/myles/ViaVersion/api/",
                "/org/slf4j/");
    }
}
