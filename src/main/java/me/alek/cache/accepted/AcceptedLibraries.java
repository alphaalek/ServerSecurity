package me.alek.cache.accepted;

import me.alek.cache.Container;
import me.alek.cache.Registery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AcceptedLibraries {

    public static class LibrariesContainer extends Container<String> {

        public final List<String> cachedLibraries = new ArrayList<>();

        @Override
        public Registery<String> getRegistery() {
            return new LibrariesRegistry(this);
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

    public static class LibrariesRegistry extends Registery<String> {

        public LibrariesRegistry(Container<String> container) {
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
}
