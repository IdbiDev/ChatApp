package me.idbi.chatapp.commands.chatcommands.roommanagers;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.commands.CommandExecutor;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.notifications.Notifications;
import me.idbi.chatapp.packets.server.MemberRoomLeavePacket;

public class RoomDisbandCommand implements CommandExecutor {
    @Override
    public boolean onCommand(Member sender, Room room, String command, String[] args) {
        if(!room.isOwner(sender)) {
            Main.getServer().sendNotification(sender, Notifications.NO_PERMISSION);
            return false;
        }

        if(args.length != 0) {
            Main.getServer().sendNotification(sender, Notifications.COMMAND_USAGE, "/disband");
            return false;
        }

        Main.getServer().sendNotification(sender, Notifications.ROOM_DISBANDED_OWNER);

        for (Member member : room.getMembers()) {
            if(member.getUniqueId().equals(sender.getUniqueId())) continue;
            Main.getServer().sendNotification(member, Notifications.ROOM_DISBANDED_MEMBERS);
        }

        room.disband();
        return true;
    }
}
