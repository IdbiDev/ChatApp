package me.idbi.chatapp.events.clients;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.view.ViewType;
import me.idbi.chatapp.view.viewmenus.GlobalBanView;
import me.idbi.chatapp.view.viewmenus.RoomChatView;
import me.idbi.chatapp.view.viewmenus.RoomListView;

@AllArgsConstructor
@Getter
public class ClientTerminalResizeEvent extends Event {
    private int oldWidth;
    private int oldHeight;
    private int newWidth;
    private int newHeight;

    @Override
    public boolean callEvent() {
        Main.getEventManager().callEvent(this);
        if(Main.getClientData().getViewManager().getView() instanceof RoomListView) {
            Main.getClientData().getViewManager().refresh();
        }
        if (Main.getClientData().getViewManager().getView() instanceof RoomChatView) {
            Main.getClientData().setPreviousWidth(oldWidth);

            Main.getClientData().setRefreshChatRoom(true);

            //Main.getClientData().refreshState(newWidth);
           // Main.getClient().sendPacket(new DebugMessagePacket("asd"));
        }
        return true;
    }
}
