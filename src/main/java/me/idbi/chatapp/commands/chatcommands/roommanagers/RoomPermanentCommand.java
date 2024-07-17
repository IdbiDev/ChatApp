package me.idbi.chatapp.commands.chatcommands.roommanagers;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.commands.CommandExecutor;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.notifications.Notifications;

public class RoomPermanentCommand implements CommandExecutor {
    @Override
    public boolean onCommand(Member sender, Room room, String command, String[] args) {
        if(!room.isOwner(sender)) {
            Main.getServer().sendNotification(sender, Notifications.NO_PERMISSION);
            return false;
        }

        if(args.length != 0) {
            Main.getServer().sendNotification(sender, Notifications.COMMAND_USAGE, "/permanent");
            return false;
        }

        if(!sender.getPermanentRooms().isEmpty()) {
            Main.getServer().sendNotification(sender, Notifications.ROOM_CREATE_TOO_MANY_ROOMS);
            return false;
        }

        room.togglePermanent();
        if(room.isPermanent()) {
            Main.getServer().sendNotification(sender, Notifications.ROOM_PERMANENT_OFF);
        } else {
            Main.getServer().sendNotification(sender, Notifications.ROOM_PERMANENT_ON);
        }
        return true;
    }
}
