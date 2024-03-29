package me.alek.serversecurity.security.injector;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Pattern;

public class ListenerInjector {

    private static Class<?> getJavaPluginClass(JavaPlugin plugin) {
        Class<?> clazz = plugin.getClass();
        while ((clazz = clazz.getSuperclass()) != null) {
            if (clazz == JavaPlugin.class) {
                return clazz;
            }
        }
        return null;
    }

    public static void inject() throws Exception {
        final SimplePluginManager simplePluginManager = (SimplePluginManager) Bukkit.getServer().getPluginManager();
        final Field fileAssociationsField = simplePluginManager.getClass().getDeclaredField("fileAssociations");
        fileAssociationsField.setAccessible(true);
        final Map<Pattern, PluginLoader> fileAssociations = (Map<Pattern, PluginLoader>) fileAssociationsField.get(simplePluginManager);

        JavaPluginLoader mainPluginLoader = null;
        if (!fileAssociations.isEmpty()) {
            Pattern key = (Pattern) fileAssociations.keySet().toArray()[0];
            mainPluginLoader = (JavaPluginLoader) fileAssociations.get(key);
        }
        if (mainPluginLoader != null) {
            final Field classLoaderField = mainPluginLoader.getClass().getDeclaredField("loaders");
            classLoaderField.setAccessible(true);

            final Map<String, Object> map = (Map<String, Object>) classLoaderField.get(mainPluginLoader);
            for (Object pluginClassLoader : map.values()) {
                injectPluginClassLoader(pluginClassLoader);
            }
        }
    }

    private static void injectPluginClassLoader(Object pluginClassLoader) {
        try {
            final Field pluginLoaderField = pluginClassLoader.getClass().getDeclaredField("loader");
            pluginLoaderField.setAccessible(true);

            final JavaPluginLoader pluginLoader = (JavaPluginLoader) pluginLoaderField.get(pluginClassLoader);
            final RegisteredListenerLoaderImpl wrappedLoader = new RegisteredListenerLoaderImpl(pluginLoader);

            Field pluginInitField = pluginClassLoader.getClass().getDeclaredField("pluginInit");
            pluginInitField.setAccessible(true);

            final JavaPlugin pluginInit = (JavaPlugin) pluginInitField.get(pluginClassLoader);
            final Class<?> javaPluginClass = getJavaPluginClass(pluginInit);
            if (javaPluginClass == null) {
                return;
            }
            final Field javaPluginLoaderField = javaPluginClass.getDeclaredField("loader");
            javaPluginLoaderField.setAccessible(true);
            javaPluginLoaderField.set(pluginInit, wrappedLoader);
        } catch (Exception ex) {
        }
    }
}
