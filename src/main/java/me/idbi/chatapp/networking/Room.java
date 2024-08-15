package me.idbi.chatapp.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.networkside.*;
import me.idbi.chatapp.networkside.Client;
import me.idbi.chatapp.networkside.Server;
import me.idbi.chatapp.notifications.Notifications;
import me.idbi.chatapp.packets.server.MemberRoomLeavePacket;
import me.idbi.chatapp.packets.server.SendMessageToClientPacket;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
public class Room implements Serializable, Comparable<Room> {

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
            throw new IllegalServerSideException();
            //throw new IllegalNetworkSideException("This function is only callable from server-side");
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
            throw new IllegalServerSideException();
            // throw new IllegalNetworkSideException("This function is only callable from server-side");
        }
        this.password = password;
        Main.getServer().refreshForKukacEveryoneUwU();
        Main.getDatabaseManager().getDriver().exec("UPDATE rooms SET password = ? WHERE uuid = ?", this.getPassword(), this.getUniqueId());

    }

    @Server
    public void setOwner(Member member) {
        if(!Main.isServer()) {
            throw new IllegalServerSideException();
            //throw new IllegalNetworkSideException("This function is only callable from server-side");
        }
        this.owner = member.getUniqueId();
        Main.getDatabaseManager().getDriver().exec("UPDATE rooms SET owner = ? WHERE uuid = ?", this.getOwner(), this.getUniqueId());

    }

    @Server
    public void rename(String newName) {
        if(!Main.isServer()) {
            throw new IllegalServerSideException();
            //throw new IllegalNetworkSideException("This function is only callable from server-side");
        }
        this.name = newName;
        Main.getServer().refreshForKukacEveryoneUwU();
        Main.getDatabaseManager().getDriver().exec("UPDATE rooms SET name = ? WHERE uuid = ?", this.name, this.uniqueId);
    }

    @Server
    public void leave(Member member) {
        if(!Main.isServer()) {
            throw new IllegalServerSideException();
            //throw new IllegalNetworkSideException("This function is only callable from server-side");
        }
        this.members.remove(member);
        Main.getServer().sendPacket(member, new MemberRoomLeavePacket(member));
        Main.getServer().refreshForKukacEveryoneUwU();
    }
    @Server
    @Client
    public boolean isFull() {
        return this.maxMembers != -1 && this.maxMembers <= this.members.size();
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
            throw new IllegalClientSideException();
            //throw new IllegalNetworkSideException("This function is only callable from client-side");
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
            throw new IllegalServerSideException();
            // throw new IllegalNetworkSideException("This function is only callable from server-side");
        }

        this.messages.add(message);
        for (Member member : this.members) {
            Main.getServer().sendPacket(member, new SendMessageToClientPacket(message));
        }
    }

    @Server
    public void togglePermanent() {
        if(!Main.isServer()) {
            throw new IllegalServerSideException();
            // throw new IllegalNetworkSideException("This function is only callable from server-side");
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
        return isOwner(member.getUniqueId());
    }

    @Override
    public int compareTo(@NotNull Room o) {
        // 1. szempont: saját szoba
        // 2. szempont: megjegyzett jelszó
        // 3. szempont: abc
        if(!Main.isServer()) {

            UUID member = Main.getClientData().getClientMember().getUniqueId();

            if(this.owner == null || o.owner == null) {
                return -1;
            }
            boolean r1Matches = this.owner.equals(member);
            boolean r2Matches = o.owner.equals(member);
            if(r1Matches) {
                return 1;
            } else if(r2Matches) {
                return 0;
            }

            boolean psw1Matches = false;
            boolean psw2Matches = false;
            if(Main.getClientData().getClientMember().getPasswords().containsKey(this.uniqueId)) {
                psw1Matches = Main.getClientData().getClientMember().getPasswords().get(this.uniqueId).equals(this.password);
            }
            if(Main.getClientData().getClientMember().getPasswords().containsKey(o.uniqueId)) {
                psw2Matches = Main.getClientData().getClientMember().getPasswords().get(o.uniqueId).equals(o.password);
            }

            if(psw1Matches) {
                return 1;
            } else if(psw2Matches) {
                return 0;
            }

        }
        return this.name.compareTo(o.getName());

//        UUID member = Main.getClientData().getClientMember().getUniqueId();
//        if(this.owner == null || o.owner == null) {
//            return this.name.compareTo(o.getName());
//        }
//
//        boolean r1Matches = this.owner.equals(member);
//        boolean r2Matches = o.owner.equals(member);
//        if (r1Matches && !r2Matches) {
//            return -1;
//        } else if (!r1Matches && r2Matches) {
//            return 1;
//        }
//
//        // Priority 2: Password saved
//        if ((Main.getClientData().getClientMember().getPasswords().containsKey(this.uniqueId)
//                && Main.getClientData().getClientMember().getPasswords().get(this.uniqueId).equals(this.password))
//                && !(Main.getClientData().getClientMember().getPasswords().containsKey(o.uniqueId)
//                && Main.getClientData().getClientMember().getPasswords().get(o.uniqueId).equals(o.password))) {
//            return -1;
//        } else if (!(Main.getClientData().getClientMember().getPasswords().containsKey(this.uniqueId)
//                && Main.getClientData().getClientMember().getPasswords().get(this.uniqueId).equals(this.password))
//                && (Main.getClientData().getClientMember().getPasswords().containsKey(o.uniqueId)
//                && Main.getClientData().getClientMember().getPasswords().get(o.uniqueId).equals(o.password))) {
//            return 1;
//        }
//
//        return this.name.compareTo(o.name);
    }
}
