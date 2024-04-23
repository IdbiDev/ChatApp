package me.idbi.chatapp.events.clients;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.client.RoomJoinPacket;
import me.idbi.chatapp.view.ViewType;
import me.idbi.chatapp.view.viewmenus.RoomListView;

import java.util.Map;

public class ClientRefreshEvent extends Event {

    private final Map<String, Room> newRooms;

    public ClientRefreshEvent(Map<String, Room> newRooms) {
        this.newRooms = newRooms;
    }

    @Override
    public boolean callEvent() {
        Main.getEventManager().callEvent(this);
        //Main.getClient().sendPacket(new RoomJoinPacket("GYVAKK Admin","cica"));
        Main.setRooms(this.newRooms);
        Main.getViewManager().refresh();
        return true;
    }
}
