package me.alek.cache.containers;

import me.alek.cache.Container;
import me.alek.cache.Registery;
import me.alek.cache.registery.SkyRageRegistery;

public class SkyRageContainer extends Container<String> {

    @Override
    public Registery<String> getRegistery() {
        return new SkyRageRegistery(this);
    }
}
