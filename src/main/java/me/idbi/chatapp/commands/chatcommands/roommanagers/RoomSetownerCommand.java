package me.idbi.chatapp.commands.chatcommands.roommanagers;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.commands.CommandExecutor;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.notifications.Notifications;

public class RoomSetownerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(Member sender, Room room, String command, String[] args) {
        if(!room.isOwner(sender)) {
            Main.getServer().sendNotification(sender, Notifications.NO_PERMISSION);
            return false;
        }

        if(args.length != 1) {
            Main.getServer().sendNotification(sender, Notifications.COMMAND_USAGE, "/setowner <név>");
            return false;
        }

        Member member = room.getMemberByName(args[0]);
        if(!member.getPermanentRooms().isEmpty()) {
            Main.getServer().sendNotification(sender, Notifications.ROOM_CREATE_TOO_MANY_ROOMS);
            return false;
        }

        room.setOwner(member);
        Main.getServer().sendNotification(sender, Notifications.ROOM_SETOWNER_SENDER);
        Main.getServer().sendNotification(member, Notifications.ROOM_SETOWNER_TARGET, room.getName());
        return false;
    }
}
