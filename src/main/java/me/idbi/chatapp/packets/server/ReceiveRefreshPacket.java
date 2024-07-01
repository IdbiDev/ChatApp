package me.idbi.chatapp.packets.server;

import lombok.Getter;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.ServerPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ReceiveRefreshPacket extends ServerPacket {
    private final Map<UUID, Room> rooms;

    public ReceiveRefreshPacket(Map<UUID, Room> rooms) {
        this.rooms = new HashMap<>(rooms);
    }
}
