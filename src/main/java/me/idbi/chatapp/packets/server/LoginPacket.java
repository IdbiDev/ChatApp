package me.idbi.chatapp.packets.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.packets.ServerPacket;

@Getter
@AllArgsConstructor
public class LoginPacket extends ServerPacket {
    private final Member loginMember;
}
