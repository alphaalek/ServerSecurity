package me.alek.serversecurity.security.injector;

import me.alek.serversecurity.security.SecurityManager;
import me.alek.serversecurity.security.blocker.AbstractListener;
import me.alek.serversecurity.security.blocker.wrappers.WrappedCommandInterceptor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
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

public class CommandInjector extends AbstractListener {

    private boolean injected = false;

    public CommandInjector(SecurityManager manager, PluginManager pluginManager) {
        super(manager, pluginManager);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void inject(ServerCommandEvent event) {
        tryInject();
    }

    @EventHandler
    public void inject(PlayerJoinEvent event) {
        tryInject();
    }

    private final List<String> labelsWrapping =
            Arrays.asList(
                    "ban", "banip", "ban-ip", "op", "deop", "unban", "unbanip",
                    "pardon", "pardonip", "pardon-ip", "stop", "kick", "kickall",
                    "whitelist", "kill", "gamemode"
            );

    private interface ModifiedCommand {

        Command getCommand();

        String getFallbackPrefix();
    }

    public void tryInject() {
        if (injected) {
            return;
        }
        injected = true;
        try {
            final SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
            final Class<?> commandMapClass = commandMap.getClass();
            final Field knownCommandsField = commandMapClass.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            final Map<String, Command> preKnownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
            final Map<String, CommandInjector.ModifiedCommand> modifiedKnownCommands = new HashMap<>();

            for (Command command : preKnownCommands.values()) {
                final Command newCommand;

                String label = command.getLabel();
                if (label.contains(":")) {
                    label = label.split(":")[1];
                }
                if (labelsWrapping.contains(label)) {
                    newCommand = new WrappedCommandInterceptor(command, command.getLabel());
                }
                else {
                    newCommand = command;
                }
                modifiedKnownCommands.put(command.getLabel(), new CommandInjector.ModifiedCommand() {
                    @Override
                    public Command getCommand() {
                        return newCommand;
                    }

                    @Override
                    public String getFallbackPrefix() {
                        String[] fallbackSplit = newCommand.getLabel().split(":");
                        if (fallbackSplit.length > 1) {
                            return fallbackSplit[0];
                        }
                        return "";
                    }
                });
            }
            commandMap.clearCommands();
            for (CommandInjector.ModifiedCommand modifiedCommand : modifiedKnownCommands.values()) {
                Command command = modifiedCommand.getCommand();
                String fallbackPrefix = modifiedCommand.getFallbackPrefix();
                commandMap.register(fallbackPrefix, command);
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

}
