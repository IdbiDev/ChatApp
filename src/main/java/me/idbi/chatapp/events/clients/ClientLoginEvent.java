package me.idbi.chatapp.events.clients;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.packets.client.RequestRefreshPacket;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.ViewType;

public class ClientLoginEvent extends Event {
    private Member clientMember;
    public ClientLoginEvent(Member clientMember) {
        this.clientMember = clientMember;
    }

    @Override
    public boolean callEvent() {
        Main.getEventManager().callEvent(this);
        Main.getClientData().getTerminalManager().center("Sikeres csatlakozás. :)", TerminalManager.Style.BOLD, TerminalManager.Color.GREEN);
        try {
            Thread.sleep(1000);
            Main.getClientData().getTerminalManager().clear();
            Main.getClient().sendPacket(new RequestRefreshPacket());

            Main.getClientData().getViewManager().setView(ViewType.ROOM_LIST);

        } catch (InterruptedException ignored) {}

        return true;
    }
}