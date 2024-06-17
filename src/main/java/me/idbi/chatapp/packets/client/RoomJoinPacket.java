package me.idbi.chatapp.packets.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.packets.ClientPacket;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RoomJoinPacket extends ClientPacket {
    private UUID uniqueId;
    private String password;
}