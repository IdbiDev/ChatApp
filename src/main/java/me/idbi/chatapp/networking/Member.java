package me.idbi.chatapp.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Member implements Serializable {
    private UUID uniqueId;
    private String name;
    private String displayName;
    private List<Room> rooms;
    private Map<UUID, String> passwords;
}
