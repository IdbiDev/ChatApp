package me.idbi.chatapp.events.clients;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.eventmanagers.interfaces.Cancellable;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.messages.ClientMessage;
import me.idbi.chatapp.networking.Client;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.notifications.Notification;
import me.idbi.chatapp.notifications.Notifications;
import me.idbi.chatapp.utils.RoomJoinResult;
import me.idbi.chatapp.view.ViewManager;
import me.idbi.chatapp.view.ViewType;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Getter
public class ClientRoomJoinEvent extends Event implements Cancellable {

    @Nullable private final Room room;
    @Setter private RoomJoinResult result;
    private Date joinAt;
    private boolean cancelled;

    public ClientRoomJoinEvent(@Nullable Room room, RoomJoinResult result, Date joinAt) {
        this.room = room;
        this.joinAt = joinAt;
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
        if (this.cancelled) {
            // System.out.println("Cancellelve a room join");
            return false;
        }

        if (this.result != RoomJoinResult.SUCCESS) {
            if (this.result == RoomJoinResult.WRONG_PASSWORD) {
                Main.getClientData().getViewManager().setView(ViewType.ROOM_JOIN);
            } else if (this.result == RoomJoinResult.ROOM_FULL) {
                Notifications.ROOM_IS_FULL.send();
                //Main.getClientData().getNotificationManager().info("Sikertelen csatlakozás", "A szoba megtelt");
            } else if (this.result == RoomJoinResult.CANCELLED) {
                Notifications.ROOM_JOIN_CANCELLED.send();
                //Main.getClientData().getNotificationManager().info("Sikertelen csatlakozás", "A csatlakozás megszakítva");
            } else if (this.result == RoomJoinResult.INVALID_ROOM) {
                Notifications.INVALID_ROOM.send();
                // Main.getClientData().getNotificationManager().info("Sikertelen csatlakozás", "Ismeretlen szoba");
            }
            //Main.getClientData().getViewManager().changeView(ViewType,
            // 25448787.ROOM_LIST);
        } else {
            if(this.room.hasPassword()) {
                Main.getClientData().getClientMember().getPasswords().put(this.room.getUniqueId(), this.room.getPassword());
            }
            Main.getClientData().getTerminalManager().clear();
            Main.getClientData().setCurrentRoom(this.room, this.joinAt);

            Main.getClientData().getViewManager().setView(ViewType.ROOM_CHAT);

//                System.out.println("called room chat ");
//                new Thread(new Client.ClientTester()).start();
//                System.out.println("Done  room chat ");
        }

        return true;
    }
}
