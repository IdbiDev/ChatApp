package me.idbi.chatapp.events.servers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Member;

@Getter
@AllArgsConstructor
public class ServerClientDisconnectEvent extends Event {
    private Member member;
    private DisconnectReason reason;

    public static enum DisconnectReason {
        BAN,
        DISCONNECT,
        CONNECTION_LOST;
    }
}
