package me.alek.scanning;

import me.alek.enums.Risk;
import me.alek.model.result.CheckResult;
import me.alek.model.ResultData;
import me.alek.utils.ChatUtils;
import me.alek.utils.AdventureUtils;
import me.alek.utils.Utils;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Loader {

    private final Player player;
    private final boolean deepScan;
    private me.alek.scanning.Scanner scanner;

    private final String PREFIX = "§8[§6AntiMalware§8] §7";

    public Loader(Player player, boolean deepScan) {
        this.player = player;
        this.deepScan = deepScan;
    }

    public Loader load(Scanner scanner) {
        this.scanner = scanner;
        return this;
    }

    public void sendFeedback() {

        Instant start = Instant.now();
        if (scanner.getResultData().isEmpty()) {
            player.sendMessage(PREFIX + "§cDer blev ikke fundet noget data fra scanningen...");
            return;
        }
        AdventureUtils json = new AdventureUtils(player);
        player.sendMessage("§8[§6AntiMalware§8] §7Scanner " + scanner.getFiles().size() + " filer for virus. Vent venligst...");
        for (ResultData data : scanner.getResultData()) {
            List<CheckResult> results = data.getResults();
            double level = data.getLevel();

            String append = "";
            if (!data.getFile().getParent().equalsIgnoreCase("plugins")) {
                append = " §7(" + data.getFile().getPath() + ")";
            }

            if (results.stream().filter(Objects::isNull).collect(Collectors.toList()).size() == results.size()) {
                if (deepScan) {
                    json.send("§a✓ " + data.getFile().getName() + append);
                }
                json.send(ChatUtils.getMessage(level, deepScan, data.getFile().getName()));
                if (deepScan) player.sendMessage("");
                continue;
            }

            if (deepScan) {
                json.send(ChatUtils.getChatSymbol(level) + "§r" + ChatUtils.getChatColor(level) + data.getFile().getName() + append);
            }
            AbstractMap.SimpleEntry<Risk, StringBuilder>[] riskStringBuilders = new AbstractMap.SimpleEntry[5];
            int i = 0;
            for (Risk risk : Risk.values()) {
                riskStringBuilders[i] = new AbstractMap.SimpleEntry<>(risk, new StringBuilder());
                i++;
            }
            boolean detected = false;
            for (CheckResult result : results) {
                if (result == null) continue;
                detected = true;
                Arrays.stream(riskStringBuilders)
                        .filter(entry -> entry.getKey() == result.getRisk())
                        .forEach(entry -> entry.getValue().append(", ").append(Utils.formatCheckResult(result)));
            }
            if (detected && deepScan) {

                boolean sendTemp = false;
                String temp = "";
                for (AbstractMap.SimpleEntry<Risk, StringBuilder> entry : riskStringBuilders) {

                    if (entry.getValue().length() < 3) continue;

                    String message = " §7- " + entry.getKey().getChatColor() + entry.getKey().getName() + ": §7" + entry.getValue().substring(2);
                    switch (entry.getKey()) {
                        case FAKE_CRITICAL: {
                            temp = entry.getValue().toString();
                            continue;
                        }
                        case HIGH: {
                            message = message + temp;
                        }
                    }
                    if (!sendTemp) {
                        if (!temp.equals("")) {
                            json.send(" §7- §cHøj risiko: §7" + temp.substring(2));
                            sendTemp = true;
                            continue;
                        }
                    }
                    player.sendMessage(message);
                }
                if (!sendTemp) {
                    if (!temp.equals("")) {
                        json.send(" §7- §cHøj risiko: §7" + temp.substring(2));
                    }
                }
            }
            json.send(ChatUtils.getMessage(level, deepScan, data.getFile().getName()));
            if (deepScan) player.sendMessage("");
        }
        //tjekker om man scanner hele plugin listen eller kun en bestemt fil
        if (scanner.getFiles().size() != 1) {

            //tjekker om serveren har mange plugins med malware, typisk enten thiccindutries eller hostflow der har smittet alle andre plugins
            double percentage = Utils.arithmeticSecure(scanner.getTotalFilesMalware(), scanner.getFiles().size());
            if (percentage >= 0.5) {
                player.sendMessage("§4⚠ Det ser ud til, at du har rigtig mange plugins med virus! Dette kan være fordi, at virussen i ét " +
                        "plugin har smittet sig til mange flere. Det anbefales at geninstallere ALLE plugins på din server, også AntiMalware selv.");
                player.sendMessage("");
            }
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        player.sendMessage("§8[§6AntiMalware§8] §7Scannede i alt " + scanner.getFiles().size() + " filer igennem og fandt " + scanner.getTotalFilesMalware()
                + " filer med virus. (" + ((double)timeElapsed.toMillis()) + "ms)");
    }
}
