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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
        return true;
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
        Runnable exit = () -> Main.getClientData().getViewManager().setView(ViewType.ROOM_LIST);

        Main.getClientData().getTerminalManager().clear();
        AtomicReference<String> name = new AtomicReference<String>();
        Main.debug("Szoba név: " + name.get());
        while(name.get() == null)
            Main.getClientData().getInputManager().getInput("Szoba neve > ", s -> {
                Main.debug(s);
                name.set(s);
            }, exit);


        AtomicReference<String> maxMembersString = new AtomicReference<String>();
        do { Main.getClientData().getInputManager().getInput("Maximum felhasználók > ", maxMembersString::set, exit); }
        while (!isNull(maxMembersString.get()) && !maxMembersString.get().matches("^[0-9]+$"));

        int maxMembers = -1;
        if(!isNull(maxMembersString.get()))
            maxMembers = Math.max(-1, Integer.parseInt(maxMembersString.get()));

        AtomicReference<String> password = new AtomicReference<String>();
        do { Main.getClientData().getInputManager().getInput("Jelszó > ", password::set, exit); }
        while (!isNull(password.get()) && !password.get().matches("^[!-~]+$"));

        // create room
        CreateRoomPacket packet = new CreateRoomPacket(name.get(), password.get(), maxMembers);
        Main.getClient().sendPacket(packet);
    }

    @Override
    public void update() {

    }

    private boolean isNull(String text) {
        return text.isEmpty() || text.isBlank();
    }
}
