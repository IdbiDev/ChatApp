package me.idbi.chatapp.events.servers;

import lombok.Getter;
import me.idbi.chatapp.eventmanagers.interfaces.Cancellable;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.messages.IMessage;

@Getter
public class ServerReceiveMessageEvent extends Event implements Cancellable {
    private IMessage message;
    private boolean cancelled;

    public ServerReceiveMessageEvent(IMessage message) {
        this.message = message;
        this.cancelled = false;
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
