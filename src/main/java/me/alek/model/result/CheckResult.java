package me.alek.model.result;

import lombok.Getter;
import me.alek.enums.Risk;

public class CheckResult {

    @Getter
    private final String detection;
    @Getter
    private final Risk risk;
    @Getter
    private final String variant;
    @Getter
    private final String className;

    public CheckResult(String detection, Risk risk, String variant, String className) {
        this.detection = detection;
        this.risk = risk;
        this.variant = variant;
        this.className = className;
    }

    public CheckResult(String detection, String variant, String className) {
        this(detection, Risk.CRITICAL, variant, className);
    }

    public CheckResult(String detection, Risk risk, String className) {
        this(detection, risk, "", className);
    }

}
