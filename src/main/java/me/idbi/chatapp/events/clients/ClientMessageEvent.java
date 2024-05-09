package me.idbi.chatapp.events.clients;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.messages.ClientMessage;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.networking.Member;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class ClientMessageEvent extends Event {
    private IMessage message;
}
