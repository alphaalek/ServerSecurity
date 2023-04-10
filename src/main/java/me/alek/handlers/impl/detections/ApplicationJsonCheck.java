package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.types.RequestPropertyHandler;

public class ApplicationJsonCheck extends RequestPropertyHandler {

    public ApplicationJsonCheck() {
        super();
    }

    @Override
    public String getType() {
        return "Application/Json";
    }

    @Override
    public Risk getRisk() {
        return Risk.LOW;

    }

    @Override
    public String[] getParams() {
        return new String[]{"application/json", "Content-Type"};
    }
}
