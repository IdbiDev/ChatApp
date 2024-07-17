package me.idbi.chatapp.commands.chatcommands.roommanagers;

import dorkbox.util.Sys;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.commands.CommandExecutor;
import me.idbi.chatapp.messages.SystemMessage;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.notifications.Notifications;
import me.idbi.chatapp.packets.server.SendMessageToClientPacket;
import me.idbi.chatapp.utils.StringPatterns;

import java.util.Date;

public class RoomRenameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(Member sender, Room room, String command, String[] args) {
        if(!room.getOwner().equals(sender.getUniqueId())) {
            Main.getServer().sendNotification(sender, Notifications.NO_PERMISSION);
            return false;
        }
        if(args.length != 1) {
            Main.getServer().sendNotification(sender, Notifications.COMMAND_USAGE, "/rename <új név>");
            return false;
        }

        if(!StringPatterns.NAME.match(args[0])) {
            Main.getServer().sendNotification(sender, Notifications.WRONG_CHARACTER);
            return false;
        }

        room.setName(args[0]);

        SystemMessage msg = new SystemMessage(
                room,
                SystemMessage.MessageType.ROOM_RENAMED.format(args[0]),
                new Date(),
                0
        );

        room.sendMessageServer(msg);

        Main.getServer().sendNotification(sender, Notifications.ROOM_RENAMED);
        Main.getServer().refreshForKukacEveryoneUwU();
        return true;
    }
}
