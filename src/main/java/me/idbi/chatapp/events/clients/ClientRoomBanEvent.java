package me.idbi.chatapp.events.clients;

import me.idbi.chatapp.eventmanagers.interfaces.Cancellable;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Room;

public class ClientRoomBanEvent extends Event implements Cancellable {

    private boolean cancelled;
    private final Room room;
    private final String reason;

    public ClientRoomBanEvent(Room room, String reason) {
        this.room = room;
        this.reason = reason;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
