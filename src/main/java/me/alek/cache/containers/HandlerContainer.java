package me.alek.cache.containers;

import me.alek.cache.Container;
import me.alek.cache.registery.HandlerRegistery;
import me.alek.cache.Registery;
import me.alek.handlers.BaseHandler;

public class HandlerContainer extends Container<BaseHandler> {

    @Override
    public Registery<BaseHandler> getRegistery() {
        return new HandlerRegistery(this);
    }

}
