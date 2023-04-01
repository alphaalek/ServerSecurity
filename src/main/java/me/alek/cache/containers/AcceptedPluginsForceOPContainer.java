package me.alek.cache.containers;

import me.alek.cache.Container;
import me.alek.cache.Registery;
import me.alek.cache.registery.AcceptedPluginsForceOPRegistery;

public class AcceptedPluginsForceOPContainer extends Container<String> {


    @Override
    public Registery<String> getRegistery() {
        return new AcceptedPluginsForceOPRegistery(this);
    }

    public boolean check(String plugin) {
        for (String cachedPlugin : getList()) {
            if (plugin.toLowerCase().contains(cachedPlugin.toLowerCase())) return true;
        }
        return false;
    }
}
