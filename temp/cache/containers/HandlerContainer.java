package me.alek.cache.containers;

import me.alek.cache.Container;
import me.alek.cache.Registery;

public class HandlerContainer extends Container<Handler> {

    @Override
    public Registery<Handler> getRegistery() {
        return new HandlerRegistery(this);
    }

}
