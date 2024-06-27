package me.idbi.chatapp.commands;

import lombok.Getter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CommandManager {
    private final Map<String, CommandExecutor> commands;

    public CommandManager() {
        this.commands = new HashMap<>();
    }

    public void callCommand(Member sender, Room room, String command, String[] args) {
        for (Map.Entry<String, CommandExecutor> cmd : this.commands.entrySet()) {
            if(cmd.getKey().equalsIgnoreCase(command)) {
                cmd.getValue().onCommand(sender, room, command, args);
            }
         }
    }

    public void registerCommand(String command, CommandExecutor executor) {
        this.commands.put(command, executor);
    }
}
