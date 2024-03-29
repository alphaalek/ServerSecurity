package me.alek.serversecurity.security.blocker.wrappers;

import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.logging.LogHolder;
import me.alek.serversecurity.security.blocker.CommandProxy;
import org.apache.logging.log4j.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
        executeAsync(commandSender, (result) -> {
            if (result) {
                delegate.execute(commandSender, s, strings);
            }
            else {
                LogHolder.getSecurityLogger().log(Level.ERROR, Lang.getMessageFormatted(Lang.SECURITY_BLOCKED_COMMAND,
                        "/" + s + " " + String.join(" ", strings)));
            }
        });
        return true;
    }

    public void executeAsync(CommandSender commandSender, Consumer<Boolean> callback) {
        if (commandSender instanceof ConsoleCommandSender) {
            CompletableFuture.supplyAsync(() -> {
                CommandProxy proxy = CommandProxy.get();
                return proxy.intercept(commandSender, this);
            }).thenAccept(callback);
        } else {
            callback.accept(true);
        }
    }


}
