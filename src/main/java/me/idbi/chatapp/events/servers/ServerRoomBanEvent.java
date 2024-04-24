package me.idbi.chatapp.events.servers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;

@AllArgsConstructor
@Getter
public class ServerRoomBanEvent extends Event {
    private Member member;
    private Room room;


}
