package me.idbi.chatapp.view.viewmenus;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.packets.client.CreateRoomPacket;
import me.idbi.chatapp.packets.client.RequestRefreshPacket;
import me.idbi.chatapp.table.Row;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;

import javax.management.InvalidAttributeValueException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
        AtomicBoolean exitBoolean = new AtomicBoolean(false);
        Runnable exit = () -> {
            exitBoolean.set(true);
            Main.getClientData().getTerminalManager().clear();
            Main.getClient().sendPacket(new RequestRefreshPacket());
            Main.getClientData().getViewManager().setView(ViewType.ROOM_LIST);
        };

        Main.getClientData().getTerminalManager().clear();
        AtomicReference<String> name = new AtomicReference<>();
        while(name.get() == null) {
            if(exitBoolean.get()) return;
            Main.getClientData().getInputManager().getInput("Szoba neve > ", name::set, exit);
        }

        if(exitBoolean.get()) return;
        AtomicReference<String> maxMembersString = new AtomicReference<>();
        do {
            if(exitBoolean.get()) return;
            Main.getClientData().getInputManager().getInput("Maximum felhasználók > ", maxMembersString::set, exit);
        }
        while (!isNull(maxMembersString.get()) && !maxMembersString.get().matches("^[0-9]+$"));

        if(exitBoolean.get()) return;
        int maxMembers = -1;
        if(!isNull(maxMembersString.get()))
            maxMembers = Math.max(-1, Integer.parseInt(maxMembersString.get()));

        AtomicReference<String> password = new AtomicReference<>();
        do {
            if(exitBoolean.get()) return;
            Main.getClientData().getInputManager().getInput("Jelszó > ", password::set, exit);
        }
        while (!isNull(password.get()) && !password.get().matches("^[!-~]+$"));

        if(exitBoolean.get()) return;

        // create room
        CreateRoomPacket packet = new CreateRoomPacket(name.get(), password.get(), maxMembers);
        Main.debug(packet.toString());
        RoomCreateConfirmView view = (RoomCreateConfirmView) ViewType.ROOM_CREATE_CONFIRM.getView();
        Main.debug("view dobne");
        view.setPacket(packet);
        Main.debug("Packed sent");
        Main.getClientData().getViewManager().setView(view);

        //Main.getClient().sendPacket(packet);
    }

    @Override
    public void update() {

    }

    private boolean isNull(String text) {
        return text == null || text.isEmpty() || text.isBlank();
    }
}
