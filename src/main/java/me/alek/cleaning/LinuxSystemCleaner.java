package me.alek.cleaning;

import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

public class LinuxSystemCleaner implements SystemCleaner {

    @Override
    public boolean isInfected() throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "systemctl status vmd-gnu"});
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("loaded")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clean(Player player) throws IOException {
        File fileVmd = new File("/bin/vmd-gnu");
        File fileService = new File("/etc/systemd/system/vmd-gnu.service");

        try {
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "systemctl stop vmd-gnu"}).waitFor();
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "systemctl disable vmd-gnu"}).waitFor();
        } catch (InterruptedException e) {
            player.sendMessage("§8[§6AntiMalware§8] §cDit system er stadig smittet! Rensning processen blev aborted.");
        }

        if (!fileVmd.canWrite() || !fileService.canWrite()) {
            player.sendMessage("§8[§6AntiMalware§8] §cDit system er stadig smittet! Sørg for, at du har permission til at skrive i filer: §7/bin/vmd-gnu and /etc/systemd/system/vmd-gnu.service");
        }

        Files.deleteIfExists(fileVmd.toPath());
        Files.deleteIfExists(fileService.toPath());
    }
}
