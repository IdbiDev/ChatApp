package me.idbi.chatapp.view.viewmenus;

import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.client.RoomJoinPacket;
import me.idbi.chatapp.view.IView;

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
        System.out.print("Jelszó > ");
        Scanner scn = new Scanner(System.in, StandardCharsets.UTF_8);

        if(scn.hasNextLine()) {
            String psw = scn.nextLine();
            Main.getClient().sendPacket(new RoomJoinPacket(room.getName(), psw));
        }
    }
}
