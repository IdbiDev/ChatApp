package me.idbi.chatapp.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.networkside.Client;
import me.idbi.chatapp.networkside.IllegalNetworkSideException;
import me.idbi.chatapp.networkside.Server;
import me.idbi.chatapp.notifications.Notifications;
import me.idbi.chatapp.packets.server.MemberRoomLeavePacket;
import me.idbi.chatapp.packets.server.SendMessageToClientPacket;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
public class Room implements Serializable {

    private UUID uniqueId;
    private String name;
    private UUID owner;
    private String password;
    private List<Member> members;
    private int maxMembers;
    private List<IMessage> messages;
    private List<UUID> administrators;
    private boolean permanent;

    @Server
    public void disband() {
        if(!Main.isServer()) {
            throw new IllegalNetworkSideException("This function is only callable from server-side");
        }
        for (Member member : this.members) {
            Main.getServer().sendPacket(member,new MemberRoomLeavePacket(member));
        }

        Main.getServer().getRooms().remove(this.uniqueId);
        Main.getServer().refreshForKukacEveryoneUwU();
        Main.getDatabaseManager().getDriver().exec("DELETE FROM rooms WHERE uuid = ?", this.uniqueId);
    }

    @Server
    public void setPassword(String password) {
        if(!Main.isServer()) {
            throw new IllegalNetworkSideException("This function is only callable from server-side");
        }
        this.password = password;
        Main.getServer().refreshForKukacEveryoneUwU();
        Main.getDatabaseManager().getDriver().exec("UPDATE rooms SET password = ? WHERE uuid = ?", this.getPassword(), this.getUniqueId());

    }

    @Server
    public void setOwner(Member member) {
        if(!Main.isServer()) {
            throw new IllegalNetworkSideException("This function is only callable from server-side");
        }
        this.owner = member.getUniqueId();
        Main.getDatabaseManager().getDriver().exec("UPDATE rooms SET owner = ? WHERE uuid = ?", this.getOwner(), this.getUniqueId());

    }

    @Server
    public void rename(String newName) {
        if(!Main.isServer()) {
            throw new IllegalNetworkSideException("This function is only callable from server-side");
        }
        this.name = newName;
        Main.getServer().refreshForKukacEveryoneUwU();
        Main.getDatabaseManager().getDriver().exec("UPDATE rooms SET name = ? WHERE uuid = ?", this.name, this.uniqueId);
    }

    @Server
    public void leave(Member member) {
        if(!Main.isServer()) {
            throw new IllegalNetworkSideException("This function is only callable from server-side");
        }
        this.members.remove(member);
        Main.getServer().sendPacket(member, new MemberRoomLeavePacket(member));
        Main.getServer().refreshForKukacEveryoneUwU();
    }
    @Server
    @Client
    public boolean isFull() {
        return this.maxMembers != 0 && this.maxMembers <= this.members.size();
    }

    @Server
    @Client
    public boolean hasPassword() {
        return this.password != null && !this.password.isBlank();
    }

    @Server
    @Client
    public void removeMember(Member member) {
        this.members.remove(member);
    }

    @Server
    @Client
    public void addMember(Member member) {
        this.members.add(member);
    }


    @Client
    public void sendMessage(IMessage message) {
        if(Main.isServer()) {
            throw new IllegalNetworkSideException("This function is only callable from client-side");
        }
        messages.add(message);
        Main.getClientData().setRefreshChatRoom(true);
    }

    @Server
    @Client
    public Member getMemberByName(String name) {
        for (Member member : this.members) {
            if (member.getDisplayName().equalsIgnoreCase(name)) {
                togglePermanent();
                return member;
            }
        }

        for (Member member : this.members) {
            if (member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }

        return null;
    }

    @Server
    public void sendMessageServer(IMessage message) {
        if(!Main.isServer()) {
            throw new IllegalNetworkSideException("This function is only callable from server-side");
        }

        this.messages.add(message);
        for (Member member : this.members) {
            Main.getServer().sendPacket(member, new SendMessageToClientPacket(message));
        }
    }

    @Server
    public void togglePermanent() {
        if(!Main.isServer()) {
            throw new IllegalNetworkSideException("This function is only callable from server-side");
        }

        if(this.permanent) {
            this.permanent = false;
        } else {
            this.permanent = true;
        }

        Main.getDatabaseManager().getDriver().exec("UPDATE rooms SET permanent = ? WHERE uuid = ?", this.permanent, this.uniqueId);
    }

    @Client
    @Server
    public boolean isOwner(UUID uniqueId) {
        if(this.owner == null) return false;
        return uniqueId.equals(this.owner);
    }

    @Client
    @Server
    public boolean isOwner(Member member) {
        if(this.owner == null) return false;
        return member.getUniqueId().equals(this.owner);
    }
}
