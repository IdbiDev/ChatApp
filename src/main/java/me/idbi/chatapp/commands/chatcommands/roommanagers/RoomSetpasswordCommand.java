package me.idbi.chatapp.commands.chatcommands.roommanagers;

import me.idbi.chatapp.commands.CommandExecutor;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.utils.StringPatterns;
import me.idbi.chatapp.utils.Utils;

public class RoomSetpasswordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(Member sender, Room room, String command, String[] args) {
        if(!room.getOwner().equals(sender)) return false;
        if(args.length != 1) return false;

        if(!StringPatterns.NAME.match(args[0])) return false;

        return false;
    }
}
