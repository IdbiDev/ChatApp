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

public class RoomJoinView implements IView {
    @Setter private Room room;

    public RoomJoinView() {
        this.room = null;
    }

    @Override
    public boolean isCursor() {
        return true;
    }

    @Override
    public void show() {
        String pw =  Main.getClientData().getInputManager().getInput("Jelszó > ");
        if(pw.equalsIgnoreCase("cancel") || pw.equalsIgnoreCase("back")) {
            Main.getClientData().getViewManager().changeView(ViewType.ROOM_LIST);
            return;
        }
        Main.getClient().sendPacket(new RoomJoinPacket(room.getUniqueId(), pw));
    }
}
