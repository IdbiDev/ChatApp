package me.idbi.chatapp.packets.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.packets.ClientPacket;

@Getter
@AllArgsConstructor
public class CreateRoomPacket extends ClientPacket {
    private final String name;
    private final String password;
    private final int maxMembers;

    public String getFormattedMaxMembers() {
        return this.maxMembers == 0 ? "Végtelen" : this.maxMembers + "";
    }
}
