package me.idbi.chatapp.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class Member implements Serializable {

    private String name;
    private List<Room> rooms;
    private Map<Room, String> passwords;
}
