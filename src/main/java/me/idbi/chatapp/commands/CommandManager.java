package me.idbi.chatapp.commands;

import lombok.Getter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CommandManager {
    private final Map<Command, CommandExecutor> commands;

    public CommandManager() {
        this.commands = new HashMap<>();
    }

    public void callCommand(Member sender, Room room, String command, String[] args) {
        for (Map.Entry<Command, CommandExecutor> cmd : this.commands.entrySet()) {
            if(cmd.getKey().getCommand().equalsIgnoreCase(command)) {
                cmd.getValue().onCommand(sender, room, command, args);
            }
         }
    }

    public void registerCommand(Command command, CommandExecutor executor) {
        this.commands.put(command, executor);
    }

    public void registerCommand(String command, CommandExecutor executor) {
        this.commands.put(new Command(command), executor);
    }

    public void registerCommand(String command, CommandExecutor executor, String... aliases) {
        this.commands.put(new Command(command, Arrays.stream(aliases).toList()), executor);
    }

    public void registerCommand(String command, String usage, CommandExecutor executor, String... aliases) {
        this.commands.put(new Command(command, usage, Arrays.stream(aliases).toList()), executor);
    }

    public void registerCommand(String command, String usage, String description, CommandExecutor executor, String... aliases) {
        this.commands.put(new Command(command, Arrays.stream(aliases).toList(), usage, description), executor);
    }
}
