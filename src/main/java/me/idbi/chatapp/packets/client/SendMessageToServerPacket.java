package me.idbi.chatapp.packets.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.messages.ClientMessage;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.packets.ClientPacket;

import java.io.Serializable;
@Getter
@AllArgsConstructor
public class SendMessageToServerPacket extends ClientPacket {
    private final IMessage message;
}
