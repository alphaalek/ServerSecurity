package me.alek.serversecurity.command;

public interface SubCommandImpl extends CommandImpl {

    String getUsage();

    String getName();

    String getDescription();

    String[] getAliases();
}
