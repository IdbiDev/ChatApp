package me.idbi.chatapp.events.clients;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.view.ViewType;
import me.idbi.chatapp.view.viewmenus.RoomListView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientRefreshEvent extends Event {

    private final Map<UUID, Room> newRooms;

    public ClientRefreshEvent(Map<UUID, Room> newRooms) {
        this.newRooms = newRooms;
    }

    @Override
    public boolean callEvent() {
        Main.getEventManager().callEvent(this);
        Main.getClientData().setRooms(new HashMap<>(this.newRooms));
        if(Main.getClientData().getViewManager().getView() instanceof RoomListView){
            Main.getClientData().getViewManager().refresh();
        }
        return true;
    }
}
