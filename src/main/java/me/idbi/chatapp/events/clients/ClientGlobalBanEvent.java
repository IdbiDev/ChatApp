package me.idbi.chatapp.events.clients;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.view.ViewType;

public class ClientGlobalBanEvent extends Event {

    private final String reason;

    public ClientGlobalBanEvent(String reason) {
        this.reason = reason;
    }
    @Override
    public boolean callEvent() {
        Main.getEventManager().callEvent(this);
        Main.getClientData().getTerminalManager().clear();
        Main.getClientData().getViewManager().setView(ViewType.GLOBAL_BAN);
        return true;
    }
}
