package me.idbi.chatapp.view.viewmenus;

import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.messages.SystemMessage;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.client.CreateRoomPacket;
import me.idbi.chatapp.packets.client.RoomJoinPacket;
import me.idbi.chatapp.packets.server.SendMessageToClientPacket;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class RoomCreateView implements IView {

    @Override
    public ViewType getType() {
        return ViewType.ROOM_CREATE;
    }
    @Override
    public boolean isCursor() {
        return true;
    }

    @Override
    public boolean hasThread() {
        return false;
    }

    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public long getUpdateInterval() {
        return -1;
    }

    @Override
    public void start() {
        Main.getClientData().getTerminalManager().clear();
        String name = null;
        while(name == null)
            name = Main.getClientData().getInputManager().getInput("Szoba neve > ");

        String maxMembersString = null;
        do { Main.getClientData().getInputManager().getInput("Maximum felhasználók > "); }
        while (!isNull(maxMembersString) && !maxMembersString.matches("^[0-9]+$"));

        int maxMembers = -1;
        if(!isNull(maxMembersString))
            maxMembers = Math.max(-1, Integer.parseInt(maxMembersString));

        String password = null;
        do { Main.getClientData().getInputManager().getInput("Jelszó > "); }
        while (!isNull(password) && !password.matches("^[!-~]+$"));

        // create room
        CreateRoomPacket packet = new CreateRoomPacket(name, password, maxMembers);
        Main.getClient().sendPacket(packet);
    }

    @Override
    public void update() {

    }

    private boolean isNull(String text) {
        return text.isEmpty() || text.isBlank();
    }
}
