package me.idbi.chatapp.events.clients;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.eventmanagers.interfaces.Cancellable;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.utils.RoomJoinResult;
import me.idbi.chatapp.view.ViewManager;
import me.idbi.chatapp.view.ViewType;
import org.jetbrains.annotations.Nullable;

@Getter
public class ClientRoomJoinEvent extends Event implements Cancellable {

    @Nullable private final Room room;
    @Setter private final RoomJoinResult result;
    private boolean cancelled;

    public ClientRoomJoinEvent(@Nullable Room room, RoomJoinResult result) {
        this.room = room;
        this.result = result;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean callEvent() {
        Main.getEventManager().callEvent(this);
        if(this.cancelled) {
            System.out.println("Cancellelve a room join");
            return false;
        } else {
            if(result != RoomJoinResult.SUCCESS) {
                Main.getViewManager().changeView(ViewType.ROOM_JOIN);
                //Main.getViewManager().changeView(ViewType,
                // 25448787.ROOM_LIST);
            } else {
                Main.setCurrentRoom(room);
                Main.getViewManager().threadedView(ViewType.ROOM_CHAT);
            }

            return true;
        }
    }
}
