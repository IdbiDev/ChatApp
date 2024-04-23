package me.idbi.chatapp.packets.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.ServerPacket;

import java.io.Serializable;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ReceiveRefreshPacket extends ServerPacket implements Serializable {
    private Map<String, Room> rooms;
}
