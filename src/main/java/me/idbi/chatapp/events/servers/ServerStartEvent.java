package me.idbi.chatapp.events.servers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.eventmanagers.interfaces.Event;

@AllArgsConstructor
@Getter
public class ServerStartEvent extends Event {
    private int port;

    @Override
    public boolean callEvent() {
        System.out.println("Server started");
        return true;
    }
}
