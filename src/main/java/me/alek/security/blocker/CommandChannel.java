package me.alek.security.blocker;

import me.alek.security.blocker.wrappers.WrappedCommandInterceptor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CommandChannel {

    private final CommandMap commandMap;
    private final HashMap<String, ResponseListener> requestedCommands;

    private static CommandChannel instance = null;

    private CommandChannel() {
        this.requestedCommands = new HashMap<>();
        this.commandMap = ((CraftServer)Bukkit.getServer()).getCommandMap();
    }

    public static synchronized CommandChannel get() {
        if (instance == null) {
            instance = new CommandChannel();
        }
        return instance;
    }

    public HashMap<String, ResponseListener> getRequestedCommands() {
        return requestedCommands;
    }

    public interface ResponseListener {

        void onAccepted();

        void onDeclined();

        WrappedCommandInterceptor getWrappedCommand();
    }

    public boolean intercept(CommandSender sender, WrappedCommandInterceptor wrappedCommand) throws CommandException {
        String id = UUID.randomUUID().toString();

        if (!(sender instanceof ConsoleCommandSender)) {
            return true;
        }

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        requestedCommands.put(id, new ResponseListener() {

            @Override
            public WrappedCommandInterceptor getWrappedCommand() {
                return wrappedCommand;
            }

            @Override
            public void onDeclined() {
                future.complete(false);
            }

            @Override
            public void onAccepted() {
                future.complete(true);
            }
        });

        try {
            boolean bool = future.get(500, TimeUnit.MILLISECONDS);
            requestedCommands.remove(id);
            return bool;
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            return true;
        }
    }

}
