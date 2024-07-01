package me.idbi.chatapp.commands.chatcommands;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.commands.CommandExecutor;
import me.idbi.chatapp.messages.SystemMessage;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.server.MemberRoomLeavePacket;
import me.idbi.chatapp.packets.server.SendMessageToClientPacket;

public class LeaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(Member sender, Room room, String command, String[] args) {
        if(command.equalsIgnoreCase("leave")) {
            // todo: leave+
            room.removeMember(sender);
            Main.getServer().sendPacket(sender,new MemberRoomLeavePacket(sender));
            SystemMessage msg = new SystemMessage(
                    room,
                    SystemMessage.MessageType.QUIT.setMember(sender),
                    1);

            for(Member m : room.getMembers()) {
                Main.getServer().sendPacket(m, new SendMessageToClientPacket(msg));
                System.out.println(m.getName());
            }
            Main.getServer().refreshForKukacEveryoneUwU();

        }
        return false;
    }
}
