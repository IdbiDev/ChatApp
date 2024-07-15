package me.idbi.chatapp.view.viewmenus;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.packets.client.CreateRoomPacket;
import me.idbi.chatapp.packets.client.RequestRefreshPacket;
import me.idbi.chatapp.utils.StringPatterns;
import me.idbi.chatapp.utils.Utils;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;

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
        while(isNull(name.get())) {
            if(exitBoolean.get()) return;
            Main.getClientData().getInputManager().getInput("Szoba neve > ", StringPatterns.NAME, name::set, exit);
        }

        if(exitBoolean.get()) return;
        AtomicReference<String> maxMembersString = new AtomicReference<>();
        while (isNull(maxMembersString.get())) {
            if(exitBoolean.get()) return;
            Main.getClientData().getInputManager().getInput("Maximum felhasználók > ", StringPatterns.NUMBER, maxMembersString::set, exit);
        }

        if(exitBoolean.get()) return;
        int maxMembers = 0;
        if(!isNull(maxMembersString.get()))
            maxMembers = Math.max(0, Integer.parseInt(maxMembersString.get()));

        AtomicReference<String> password = new AtomicReference<>();
        while (isNull(password.get())) {
            if(exitBoolean.get()) return;
            Main.getClientData().getInputManager().getInput("Jelszó > ", StringPatterns.PASSWORD, password::set, exit);
        }

        if(exitBoolean.get()) return;

        // create room
        CreateRoomPacket packet = new CreateRoomPacket(name.get(), password.get(), maxMembers);
        Main.debug(packet.toString());
        RoomCreateConfirmView view = (RoomCreateConfirmView) ViewType.ROOM_CREATE_CONFIRM.getView();
        view.setPacket(packet);
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
