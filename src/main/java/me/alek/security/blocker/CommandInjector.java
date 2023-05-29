package me.alek.security.blocker;

import me.alek.security.SecurityManager;
import me.alek.security.blocker.wrappers.WrappedCommandInterceptor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.command.VanillaCommandWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CommandInjector extends AbstractListener {

    private boolean injected;
    private final List<String> labelsWrapping =
            Arrays.asList(
                    "ban",
                    "banip",
                    "ban-ip",
                    "op",
                    "deop",
                    "unban",
                    "unbanip",
                    "pardon",
                    "pardonip",
                    "pardon-ip",
                    "stop",
                    "kick",
                    "kickall",
                    "whitelist",
                    "kill",
                    "gamemode");

    public CommandInjector(SecurityManager manager, PluginManager pluginManager) {
        super(manager, pluginManager);
        injected = false;
    }

    private interface ModifiedCommand {

        Command getCommand();

        String getFallbackPrefix();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void inject(ServerCommandEvent event) {
        if (!injected) {
            inject();
        }
    }

    @EventHandler
    public void inject(PlayerJoinEvent event) {
        if (!injected) {
            inject();
        }
    }

    public void inject() {
        injected = true;
        try {
            final SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
            final Class<?> commandMapClass = commandMap.getClass();
            final Field knownCommandsField = commandMapClass.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            final Map<String, Command> preKnownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
            final Map<String, ModifiedCommand> modifiedKnownCommands = new HashMap<>();

            for (Command command : preKnownCommands.values()) {

                final AtomicReference<Command> newCommand = new AtomicReference<>();

                String label = command.getLabel();
                if (label.contains(":")) {
                    label = label.split(":")[1];
                }

                if (labelsWrapping.contains(label)) {
                    WrappedCommandInterceptor wrappedCommandInterceptor = new WrappedCommandInterceptor(command, command.getLabel());
                    newCommand.set(wrappedCommandInterceptor);
                }
                if (newCommand.get() == null) {
                    newCommand.set(command);
                }
                modifiedKnownCommands.put(command.getLabel(), new ModifiedCommand() {
                    @Override
                    public Command getCommand() {
                        return newCommand.get();
                    }

                    @Override
                    public String getFallbackPrefix() {
                        String fallbackPrefix = "";
                        String[] fallbackSplit = newCommand.get().getLabel().split(":");
                        if (fallbackSplit.length > 1) {
                            fallbackPrefix = fallbackSplit[0];
                        }
                        return fallbackPrefix;
                    }
                });
            }
            commandMap.clearCommands();
            for (ModifiedCommand modifiedCommand : modifiedKnownCommands.values()) {
                Command command = modifiedCommand.getCommand();
                String fallbackPrefix = modifiedCommand.getFallbackPrefix();
                commandMap.register(fallbackPrefix, command);
            }

        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
}
