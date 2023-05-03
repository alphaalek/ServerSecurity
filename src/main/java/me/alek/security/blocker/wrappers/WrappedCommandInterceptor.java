package me.alek.security.blocker.wrappers;

import me.alek.security.blocker.CommandChannel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class WrappedCommandInterceptor extends Command {

    private Command delegate;

    protected WrappedCommandInterceptor(String name) {
        super(name);
        this.delegate = null;
    }

    public WrappedCommandInterceptor(Command command, String name) {
        this(name);
        this.delegate = command;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (commandSender instanceof ConsoleCommandSender) {

            ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
            executor.execute(() -> {

                CommandChannel channel = CommandChannel.get();
                future.complete(channel.intercept(commandSender, this));
            });
        } else {
            future.complete(true);
        }
        AtomicBoolean bool = new AtomicBoolean();
        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                bool.set(true);
            } else if (!result) {
                Bukkit.broadcastMessage("Farlig kommando blev blokeret");
                bool.set(false);
            } else {
                bool.set(delegate.execute(commandSender, s, strings));
            }
        });
        return bool.get();
    }


}
