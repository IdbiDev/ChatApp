package me.idbi.chatapp.packets.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.packets.ClientPacket;

@Getter
@AllArgsConstructor
public class CreateRoomPacket extends ClientPacket {
    private String name;
    private String password;
    private int maxMembers;

    public String getFormattedMaxMembers() {
        return this.maxMembers == -1 ? "Végtelen" : this.maxMembers + "";
    }
}
