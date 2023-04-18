package me.alek.security.config;

import me.alek.AntiMalwarePlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class ResourceProviderWrapper {

    private final AntiMalwarePlugin plugin;

    public ResourceProviderWrapper(final AntiMalwarePlugin plugin) {
        this.plugin = plugin;
    }

    public InputStream getResource(final String fileName) {
        return this.plugin.getResource(fileName);
    }

    public void saveResource(final File file, final String resourcePath) throws IOException {
        final InputStream template = getResource(resourcePath);
        final OutputStream outputStream = Files.newOutputStream(file.toPath());

        byte[] buffer = new byte[1024];
        int len;
        while ((len = template.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        template.close();
        outputStream.flush();
        outputStream.close();
    }
}
