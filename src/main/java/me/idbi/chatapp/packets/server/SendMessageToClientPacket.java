package me.idbi.chatapp.packets.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.packets.ServerPacket;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class SendMessageToClientPacket extends ServerPacket {
    private IMessage message;
}
