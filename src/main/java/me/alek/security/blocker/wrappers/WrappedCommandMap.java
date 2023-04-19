package me.alek.security.blocker.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class WrappedCommandMap implements CommandMap {

    private final CommandMap delegate;
    private final HashMap<String, ResponseListener> requestedCommands;
    private final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", Pattern.LITERAL);

    public WrappedCommandMap(CommandMap delegate) {
        this.delegate = delegate;
        this.requestedCommands = new HashMap<>();
    }

    public HashMap<String, ResponseListener> getRequestedCommands() {
        return requestedCommands;
    }

    public interface ResponseListener {

        void onAccepted();

        void onDeclined();

        String getCommandLine();
    }

    private boolean shouldReturnFalse(CommandSender sender, String commandLine) {
        String[] args = PATTERN_ON_SPACE.split(commandLine);
        if (args.length == 0) {
            return true;
        }

        String sentCommandLabel = args[0].toLowerCase();
        Command target = getCommand(sentCommandLabel);

        if (target == null) {
            return true;
        }

        return false;
    }

    @Override
    public boolean dispatch(CommandSender sender, String commandLine) throws CommandException {
        String id = UUID.randomUUID().toString();
        Bukkit.broadcastMessage("afventer svar " + id + "...");

        if (!(sender instanceof ConsoleCommandSender)) {
            Bukkit.broadcastMessage("executing " + id + "...");
            return delegate.dispatch(sender, commandLine);
        }

        if (shouldReturnFalse(sender, commandLine)) {
            return false;
        }

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        ResponseListener listener = new ResponseListener() {

            @Override
            public String getCommandLine() {
                return commandLine;
            }

            @Override
            public void onAccepted() {
                Bukkit.broadcastMessage("cancelling " + id + "...");
                requestedCommands.remove(id);
                future.complete(false);
            }

            @Override
            public void onDeclined() {
                Bukkit.broadcastMessage("executing " + id + "...");
                requestedCommands.remove(id);
                delegate.dispatch(sender, commandLine);
                future.complete(true);
            }
        };
        requestedCommands.put(id, listener);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(() -> {
            if (!future.isDone()) {
                Bukkit.broadcastMessage("Timeout reached, completing future " + id + "...");
                requestedCommands.remove(id);
                future.complete(true);
            }
        }, 500, TimeUnit.MILLISECONDS);

        AtomicBoolean bool = new AtomicBoolean(false);
        CompletableFuture.runAsync(() -> {
            try {
                boolean shouldExecute = future.get();
                if (shouldExecute) {
                    bool.set(delegate.dispatch(sender, commandLine));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        return bool.get();
    }



    @Override
    public void registerAll(String s, List<Command> list) {
        delegate.registerAll(s, list);
    }

    @Override
    public boolean register(String s, String s1, Command command) {
        return register(s, s1, command);
    }

    @Override
    public boolean register(String s, Command command) {
        return delegate.register(s, command);
    }

    @Override
    public void clearCommands() {
        delegate.clearCommands();
    }

    @Override
    public Command getCommand(String s) {
        return delegate.getCommand(s);
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String s) throws IllegalArgumentException {
        return delegate.tabComplete(commandSender, s);
    }
}
