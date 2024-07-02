package me.idbi.chatapp.commands.chatcommands.roommanagers;

import me.idbi.chatapp.commands.CommandExecutor;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;

public class RoomSetpasswordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(Member sender, Room room, String command, String[] args) {
        return false;
    }
}
