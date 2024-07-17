package me.idbi.chatapp.packets.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.packets.ClientPacket;

import java.io.Serializable;
@Getter
@AllArgsConstructor
public class DebugMessagePacket extends ClientPacket {
    private final String message;
}
