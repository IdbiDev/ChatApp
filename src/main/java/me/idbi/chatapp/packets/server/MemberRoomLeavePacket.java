package me.idbi.chatapp.packets.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.packets.ServerPacket;

@Getter
@AllArgsConstructor
public class MemberRoomLeavePacket extends ServerPacket {
    private Member member;
}
