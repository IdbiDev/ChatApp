package me.idbi.chatapp.commands.chatcommands.roommanagers;

import me.idbi.chatapp.commands.CommandExecutor;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.utils.StringPatterns;
import me.idbi.chatapp.utils.Utils;

public class RoomSetpasswordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(Member sender, Room room, String command, String[] args) {
        if(!room.getOwner().equals(sender.getUniqueId())) {
            Main.getServer().sendNotification(sender, Notifications.NO_PERMISSION);
            return false;
        }
        if(args.length != 1) {
            Main.getServer().sendNotification(sender, Notifications.COMMAND_USAGE, "/setpassword <új jelszó>");
            return false;
        }

        if(!StringPatterns.PASSWORD.match(args[0])) {
            Main.getServer().sendNotification(sender, Notifications.WRONG_CHARACTER);
            return false;
        }

        return false;
    }
}
