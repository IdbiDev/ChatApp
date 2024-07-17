package me.idbi.chatapp.packets.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.ServerPacket;
import me.idbi.chatapp.utils.RoomJoinResult;

import java.io.Serializable;
import java.util.Date;

@Getter
@AllArgsConstructor
public class RoomJoinResultPacket extends ServerPacket {
    private final RoomJoinResult result;
    private final Room room;
    private final Date joinAt;
}