package me.idbi.chatapp.packets.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.packets.ClientPacket;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class RoomJoinPacket extends ClientPacket implements Serializable {
    private String name;
    private String password;
}