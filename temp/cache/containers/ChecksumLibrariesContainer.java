package me.alek.cache.containers;

import me.alek.cache.Container;
import me.alek.cache.Registery;

import java.util.ArrayList;
import java.util.List;

public class ChecksumLibrariesContainer extends Container<String> {

    public final List<String> cachedLibraries = new ArrayList<>();

    @Override
    public Registery<String> getRegistery() {
        return new ChecksumLibrariesRegistery(this);
    }

    public boolean check(String classPath) {
        for (String cachedLibrary : cachedLibraries) {
            if (classPath.contains(cachedLibrary)) {
                return true;
            }
        }
        for (String library : getList()) {
            if (classPath.contains(library)) {
                cachedLibraries.add(library);
                getList().remove(library);
                return true;
            }
        }
        return false;
    }
}
