package me.idbi.chatapp.packets.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.packets.ClientPacket;

import java.util.UUID;

@Getter
public class RoomEditPacket extends ClientPacket {
    public static enum RoomEditType {
        RENAME,
        SET_PASSWORD,
        TRANSFER_OWNERSHIP,
        SET_MAX_MEMBERS
    }
    private final RoomEditType type;
    private final Object value;
    private final UUID uniqueId;

    public RoomEditPacket(UUID uniqueId, RoomEditType type, Object value) {
        this.type = type;
        this.value = value;
        this.uniqueId = uniqueId;
    }
}
