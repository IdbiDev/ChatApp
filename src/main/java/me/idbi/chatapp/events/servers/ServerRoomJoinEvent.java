package me.idbi.chatapp.events.servers;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.eventmanagers.interfaces.Cancellable;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.eventmanagers.interfaces.Listener;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.utils.RoomJoinResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerRoomJoinEvent extends Event implements Listener, Cancellable {

    @Nullable
    private final Room room;
    @NotNull
    private final Member member;
    @Setter
    @Getter
    private final RoomJoinResult result;
    private boolean cancelled;

    public ServerRoomJoinEvent(@NotNull Member member, @Nullable Room room, RoomJoinResult result) {
        this.room = room;
        this.member = member;
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
}
