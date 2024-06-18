package me.idbi.chatapp.view.viewmenus;

import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.client.RoomJoinPacket;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class RoomJoinView implements IView {
    @Setter private Room room;

    @Override
    public ViewType getType() {
        return ViewType.ROOM_JOIN;
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
        return false;
    }

    @Override
    public long getUpdateInterval() {
        return -1;
    }

    @Override
    public void start() {
        AtomicReference<String> pw = new AtomicReference<>();
        Main.getClientData().getInputManager().getInput("Jelszó > ", pw::set, () -> Main.getClientData().getViewManager().setView(ViewType.ROOM_LIST));
        if(pw.get().equalsIgnoreCase("cancel") || pw.get().equalsIgnoreCase("back")) {
            Main.getClientData().getViewManager().setView(ViewType.ROOM_LIST);
            return;
        }
        Main.getClient().sendPacket(new RoomJoinPacket(this.room.getUniqueId(), pw));
    }

    @Override
    public void update() {

    }
}
