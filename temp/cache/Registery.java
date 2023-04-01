package me.alek.cache;

import java.util.List;

public abstract class Registery<T> {

    private final Container<T> container;

    public Registery(Container<T> container) {
        this.container = container;
        for (T element : getElements()) {
            container.add(element);
        }
    }

    public abstract List<T> getElements();
}
