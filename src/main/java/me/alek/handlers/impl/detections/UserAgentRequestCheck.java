package me.alek.handlers.impl.detections;

import me.alek.enums.Risk;
import me.alek.handlers.types.RequestPropertyHandler;

public class UserAgentRequestCheck extends RequestPropertyHandler {

    public UserAgentRequestCheck() {
        super();
    }

    @Override
    public String[] getParams() {
        return new String[]{"", "User-Agent"};
    }


    @Override
    public String getType() {
        return "User-Agent Request";
    }

    @Override
    public Risk getRisk() {
        return Risk.MODERATE;
    }
}
