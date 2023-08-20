package me.alek.serversecurity.model;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginProperties {

    private Properties properties = null;

    public PluginProperties(File file) {
        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if (entry.getName().contains("plugin.yml")) {
                    Properties properties = new Properties();
                    InputStream inputStream = jarFile.getInputStream(entry);
                    properties.load(inputStream);

                    this.properties = properties;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSourceLib() {
        if (getProperties() == null) return null;
        String mainClassPath = getProperties().getProperty("main");
        if (mainClassPath  == null) return null;

        mainClassPath = mainClassPath.replaceAll("\\.", "/");
        return StringUtils.substringBeforeLast(mainClassPath, "/") + "/";
    }

    public String getPluginName() {
        return properties.getProperty("name");
    }

    public String getVersion() {
        return properties.getProperty("version");
    }

    public String getAuthor() {
        return properties.getProperty("author");
    }

    public Properties getProperties() {
        return properties;
    }
}
