package me.alek.serversecurity.command;

public interface SubCommandImpl extends CommandImpl {

    boolean executableByConsole();

    String getUsage();

    String getName();

    String getDescription();

    String[] getAliases();
}
