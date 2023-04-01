package me.alek.model;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

public class ResultData {

    @Getter
    private final File file;
    @Getter
    private final List<CheckResult> results;

    @Getter
    private final double level;

    public ResultData(List<CheckResult> results, File file, double level) {
        this.file = file;
        this.results = results;
        this.level = level;
    }
}
