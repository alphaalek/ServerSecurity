package me.alek.scanning;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class ScanStatus {

    enum State {
        SCANNING, DONE, UNKNOWN
    }

    @Getter @Setter
    private State state;
    @Getter
    private final File file;

    public ScanStatus(File file) {
        this.file = file;
        this.state = State.SCANNING;
    }
}
