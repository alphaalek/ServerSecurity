package me.alek.cache.containers;

import me.alek.cache.Container;
import me.alek.cache.Registery;
import me.alek.obfuscation.handlers.AbstractObfHandler;

public class ObfuscationContainer extends Container<AbstractObfHandler> {

    @Override
    public Registery<AbstractObfHandler> getRegistery() {
        return new ObfuscationRegistery(this);
    }
}
