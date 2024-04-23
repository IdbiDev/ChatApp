package me.idbi.chatapp.packets.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.packets.ServerPacket;

import java.io.Serializable;
@Getter
@AllArgsConstructor
public class LoginPacket extends ServerPacket implements Serializable {
}
