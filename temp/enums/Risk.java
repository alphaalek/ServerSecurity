package me.alek.enums;

import lombok.Getter;

public enum Risk {


    CRITICAL(3, 30,"§4", "Malware"),
    HIGH(2, 5,"§c", "Høj risiko"),
    MODERATE(2, 3,"§e", "Moderat risiko"),
    LOW(1, 1,"§a", "Lav risiko");

    @Getter
    private final double obfuscationLevel;

    @Getter
    private final double detectionLevel;

    @Getter
    private final String chatColor;

    @Getter
    private final String name;


    Risk(double obfuscationLevel, double detectionLevel, String chatColor, String name) {
        this.obfuscationLevel = obfuscationLevel;
        this.detectionLevel = detectionLevel;
        this.chatColor = chatColor;
        this.name = name;
    }
}
