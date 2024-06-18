package me.idbi.chatapp.events.servers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Room;

@Getter
@AllArgsConstructor
public class ServerRoomCreateEvent extends Event {
    private Room room;
}
