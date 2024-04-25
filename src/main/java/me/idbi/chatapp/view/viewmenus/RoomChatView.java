package me.idbi.chatapp.view.viewmenus;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.messages.ClientMessage;
import me.idbi.chatapp.networking.Client;
import me.idbi.chatapp.packets.client.SendMessageToServerPacket;
import me.idbi.chatapp.view.IView;

import java.util.Date;

public class RoomChatView implements IView {
    @Override
    public boolean isCursor() {
        return false;
    }

    @Override
    public void show() {
        Main.getTerminalManager().clear();
        Main.getClient().sendPacket(new SendMessageToServerPacket(
                new ClientMessage(Main.getClient().getName(), Main.getCurrentRoom(), "HEYYY",new Date())
        ));
    }
}
