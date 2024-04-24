package me.idbi.chatapp.events.servers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Server;

@AllArgsConstructor
@Getter
public class ServerGlobalBanEvent extends Event {
    private Member member;
    private String reason;
}
