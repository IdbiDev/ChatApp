package me.idbi.chatapp.commands;

import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;

public interface CommandExecutor {

    public boolean onCommand(Member sender, Room room, String command, String[] args);
}
