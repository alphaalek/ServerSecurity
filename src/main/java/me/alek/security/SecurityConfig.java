package me.alek.security;

import com.google.common.base.Charsets;
import me.alek.AntiMalwarePlugin;
import me.alek.security.SecurityManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;

public class SecurityConfig {

    private YamlConfiguration yamlConfiguration;
    private final File file;
    private final SecurityManager manager;

    public SecurityConfig(SecurityManager manager) {
        this.manager = manager;
        File dataFolder = AntiMalwarePlugin.getInstance().getDataFolder();
        dataFolder.mkdir();
        this.file = new File(dataFolder, "security.yml");
        if (!file.exists()) {
            boolean success = false;
            try {
                success = file.createNewFile();
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
            if (!success) return;
            try {
                manager.getResourceProvider().saveResource(file, "security.yml");
            } catch (IOException ex) {
            }
        }
        this.yamlConfiguration = load(this.file);
        if (this.yamlConfiguration == null) return;
        save();
    }

    public void reload() {
        this.yamlConfiguration = load(this.file);
        if (this.yamlConfiguration == null) return;
        Reader configStream = null;
        final InputStream resourceStream = this.manager.getResourceProvider().getResource(this.file.getName());
        if (resourceStream != null) {
            configStream = new InputStreamReader(resourceStream, Charsets.UTF_8);
        }
        if (configStream != null) {
            final YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(configStream);
            this.yamlConfiguration.setDefaults(defaultConfig);
        }
    }

    public void save() {
        if (this.yamlConfiguration == null) {
            return;
        }
        try {
            this.yamlConfiguration.save(this.file);
        } catch (final IOException ex) {
        }
    }

    public YamlConfiguration load(final File file) {
        if (file == null) {
            return null;
        }
        final YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (final FileNotFoundException ex) {
        } catch (final IOException | InvalidConfigurationException ex) {
        }
        return config;
    }

    public YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration;
    }

    public File getFile() {
        return file;
    }
}
