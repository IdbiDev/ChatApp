package me.idbi.chatapp.commands.chatcommands.roommanagers;

import me.idbi.chatapp.commands.CommandExecutor;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.notifications.Notification;
import me.idbi.chatapp.utils.Utils;

public class RoomRenameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(Member sender, Room room, String command, String[] args) {
        if(!room.getOwner().equals(sender)) return false;
        if(args.length != 1) return false;

        if(!Utils.getNamePattern().matcher(args[0]).matches()) {
            Notification.Notifications.WRONG_RENAME.send();
            return false;
        }


        return false;
    }
}
