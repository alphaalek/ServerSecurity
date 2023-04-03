package me.alek.utils;

import me.alek.AntiMalwarePlugin;

import org.bukkit.Bukkit;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class UpdateChecker {


    private final AntiMalwarePlugin plugin;
    private final int resourceId;

    public UpdateChecker(AntiMalwarePlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException e) {
                Logger logger = Logger.getLogger("Minecraft");
                logger.severe("[AntiMalware] Kunne ikke åbne connection til spigot legacy api, og ved derfor ikke om pluginnet bruger den seneste version!");
            }
        });
    }
}
