package me.alek.security.blocker.wrappers;

import me.alek.logging.LogHolder;
import me.alek.security.blocker.CommandChannel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

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

        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (commandSender instanceof ConsoleCommandSender) {

            final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
            executor.execute(() -> {

                CommandChannel channel = CommandChannel.get();
                future.complete(channel.intercept(commandSender, this));
            });

        } else {
            future.complete(true);
            return delegate.execute(commandSender, s, strings);
        }

        final AtomicBoolean bool = new AtomicBoolean();
        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                bool.set(true);
            }
            else {
                if (!result) {
                    LogHolder.getSecurityLogger().log(Level.SEVERE, "Kommando blev blokeret: CONSOLE: /"  + s + " " + String.join(" ", strings));
                    bool.set(false);
                }
                else {
                    bool.set(delegate.execute(commandSender, s, strings));
                }
            }
        });
        return bool.get();
    }


}
