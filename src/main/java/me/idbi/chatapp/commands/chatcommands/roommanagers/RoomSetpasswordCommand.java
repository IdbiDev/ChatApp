package me.idbi.chatapp.commands.chatcommands.roommanagers;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.commands.CommandExecutor;
import me.idbi.chatapp.messages.SystemMessage;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.notifications.Notification;
import me.idbi.chatapp.notifications.Notifications;
import me.idbi.chatapp.utils.StringPatterns;

import java.util.Date;

public class RoomSetpasswordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(Member sender, Room room, String command, String[] args) {
        if(!room.isOwner(sender)) {
            Main.getServer().sendNotification(sender, Notifications.NO_PERMISSION);
            return false;
        }
        if(args.length != 1) {
            Main.getServer().sendNotification(sender, Notifications.COMMAND_USAGE, "/permanent");
            return false;
        }

        if(!StringPatterns.PASSWORD.match(args[0])) {
            Main.getServer().sendNotification(sender, Notifications.WRONG_TEXT_INPUT);
            return false;
        }

        room.setPassword(args[0]);

        SystemMessage msg = new SystemMessage(
                room,
                SystemMessage.MessageType.ROOM_PASSWORD_CHANGED.getMessage(),
                new Date(),
                0
        );

        room.sendMessageServer(msg);

        Main.getServer().sendNotification(sender, Notifications.ROOM_PASSWORD_CHANGED, args[0]);
        return true;
    }
}
