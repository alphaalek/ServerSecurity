package me.alek.cache.registery;

import me.alek.cache.Container;
import me.alek.cache.Registery;

import java.util.Arrays;
import java.util.List;

public class AcceptedNameObfRegistery extends Registery<String> {

    public AcceptedNameObfRegistery(Container<String> container) {
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
