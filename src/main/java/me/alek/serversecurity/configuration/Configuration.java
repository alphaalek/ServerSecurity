package me.alek.serversecurity.configuration;

import me.alek.serversecurity.ServerSecurityPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class Configuration {

    private FileConfiguration yamlConfiguration;
    private File file;
    private ConfigurationOptions options;

    public Configuration() {
        File dataFolder = ServerSecurityPlugin.get().getDataFolder();
        dataFolder.mkdirs();
        ServerSecurityPlugin.get().getConfig().options().copyDefaults(true);
        ServerSecurityPlugin.get().saveDefaultConfig();

        this.yamlConfiguration = loadConfiguration();
        this.options = new ConfigurationOptions(this);
    }

    public ConfigurationOptions getOptions() {
        return options;
    }

    public void generatePluginOptions() {
        this.options = new ConfigurationOptions(this);
    }

    public FileConfiguration loadConfiguration() {
        File dataFolder = ServerSecurityPlugin.get().getDataFolder();
        file = new File(dataFolder, "config.yml");
        if (!file.exists()) {
            ServerSecurityPlugin.get().saveDefaultConfig();
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public synchronized void reload() {
        ServerSecurityPlugin.get().reloadConfig();

        generatePluginOptions();
    }

    public YamlConfiguration load(final File file) {
        if (file == null) {
            return null;
        }
        final YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (final IOException | InvalidConfigurationException ex) {
        }
        return config;
    }

    public FileConfiguration getFileConfiguration() {
        return yamlConfiguration;
    }

}
