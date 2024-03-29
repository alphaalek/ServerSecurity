package me.alek.serversecurity.security.injector;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class PluginLoaderAdapter implements PluginLoader {

    private final JavaPluginLoader delegate;

    public PluginLoaderAdapter(JavaPluginLoader pluginLoader) {
        this.delegate = pluginLoader;
    }

    @Override
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin plugin) {
        return delegate.createRegisteredListeners(listener, plugin);
    }

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
        return delegate.loadPlugin(file);
    }

    @Override
    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        return delegate.getPluginDescription(file);
    }

    @Override
    public Pattern[] getPluginFileFilters() {
        return delegate.getPluginFileFilters();
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        delegate.enablePlugin(plugin);
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        delegate.disablePlugin(plugin);
    }
}
