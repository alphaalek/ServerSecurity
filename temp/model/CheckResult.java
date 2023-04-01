package me.alek.model;

import lombok.Getter;
import me.alek.enums.Risk;
import me.alek.handlers.Check;

public class CheckResult {

    @Getter
    private final String detection;

    private final boolean malware;

    @Getter
    private final Risk risk;

    @Getter
    private final String variant;


    public CheckResult(String detection, boolean malware, Risk risk, String variant) {
        this.detection = detection;
        this.malware = malware;
        this.risk = risk;
        this.variant = variant;
    }

    public CheckResult(String detection, boolean malware, String variant) {
        this(detection, malware, Risk.CRITICAL, variant);
    }

    public CheckResult(String detection, Risk risk, String variant) {
        this(detection, false, risk, variant);
    }

    public CheckResult(String detection, Risk risk) {
        this(detection, false, risk, "");
    }

    public boolean isMalware() {
        return malware;
    }




}
