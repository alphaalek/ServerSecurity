package me.alek.cache.containers;

import me.alek.cache.Container;
import me.alek.cache.Registery;
import me.alek.cache.registery.AcceptedNameObfRegistery;

public class AcceptedNameObfContainer extends Container<String> {

    @Override
    public Registery<String> getRegistery() {
        return new AcceptedNameObfRegistery(this);
    }
}
