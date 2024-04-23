package me.idbi.chatapp.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class Room implements Serializable {

    private String name;
    private Member owner;
    private String password;
    private List<Member> members;
    private int maxMembers;

    public boolean hasPassword() {
        return this.password != null;
    }
}
