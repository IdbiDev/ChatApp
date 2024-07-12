package me.idbi.chatapp.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.messages.ClientMessage;
import me.idbi.chatapp.messages.IMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@AllArgsConstructor
@Setter
public class Room implements Serializable {

    private UUID uniqueId;
    private String name;
    private UUID owner;
    private String password;
    private List<Member> members;
    private int maxMembers;
    private List<IMessage> messages;
    private List<UUID> administrators;

    public boolean isFull() {
        return this.maxMembers != -1 && this.maxMembers <= this.members.size();
    }

    public boolean hasPassword() {
        return this.password != null && !this.password.isBlank();
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
