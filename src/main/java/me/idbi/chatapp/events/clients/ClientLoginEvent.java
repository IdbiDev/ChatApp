package me.idbi.chatapp.events.clients;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.packets.client.RequestRefreshPacket;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.ViewType;

public class ClientLoginEvent extends Event {

    public ClientLoginEvent() {

    }

    @Override
    public boolean callEvent() {
        Main.getEventManager().callEvent(this);
        Main.getTerminalManager().center("Sikeres csatlakozás. :)", TerminalManager.Style.BOLD, TerminalManager.Color.GREEN);
        try {
            Thread.sleep(1000);
            Main.getTerminalManager().clear();
            Main.getClient().sendPacket(new RequestRefreshPacket());
            Main.getViewManager().changeView(ViewType.ROOM_LIST);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}