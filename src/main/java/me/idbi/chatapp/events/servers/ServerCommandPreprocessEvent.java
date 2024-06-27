package me.idbi.chatapp.events.servers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;

@AllArgsConstructor
@Getter
@Setter
public class ServerCommandPreprocessEvent extends Event {

    private Member member;
    private Room room;
    private String command;
    private String[] args;
}
