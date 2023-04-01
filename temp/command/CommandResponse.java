package me.alek.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandResponse {

    public static Response check(AbstractCommand abstractCommand, CommandSender sender, String[] args) {
        if (args.length == 0) return Response.PERFORM_SINGLE;

        if (!abstractCommand.executableAsConsole()) {
            if (sender instanceof ConsoleCommandSender) return Response.NOT_EXECUTABLE;

        }
        if (abstractCommand.getPermission() != null) {
            if (!sender.hasPermission(abstractCommand.getPermission())) return Response.NO_PERMISSION;
        }
        return Response.SUCCESS;
    }

    public enum Response {
        SUCCESS,
        PERFORM_SINGLE,
        NOT_EXECUTABLE,
        NO_PERMISSION
    }
}
