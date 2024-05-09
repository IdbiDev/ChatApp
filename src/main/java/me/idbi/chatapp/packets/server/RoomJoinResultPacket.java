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
public class RoomJoinResultPacket extends ServerPacket implements Serializable {
    private RoomJoinResult result;
    private Room room;
    private Date joinAt;
}