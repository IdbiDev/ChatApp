package me.idbi.chatapp.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.messages.ClientMessage;
import me.idbi.chatapp.messages.IMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class Room implements Serializable {

    private String name;
    private Member owner;
    private String password;
    private List<Member> members;
    private int maxMembers;
    private List<ClientMessage> messages;

    public boolean hasPassword() {
        return this.password != null;
    }

    public void removeMember(Member member) {
        this.members.remove(member);
    }
    public void addMember(Member member) {
        this.members.add(member);
    }

    public void sendMessage(IMessage message) {
        messages.add(message);
        Main.getClientData().setRefreshChatRoom(true);
    }
}
