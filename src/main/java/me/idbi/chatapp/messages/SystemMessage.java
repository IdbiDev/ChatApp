package me.idbi.chatapp.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class SystemMessage implements IMessage, Serializable {

    @AllArgsConstructor
    public static enum MessageType {
        ROOM_CREATE("%room% szoba létrehozva."),
        BAN("%member% kitiltva a szobából."),
        KICK("%member% kirúgva a szobából."),
        JOIN("%member% csatlakozott."),
        QUIT("%member% kilépett."),
        BROADCAST("%s");

        private final String message;

        public String setMember(String name) {
            return this.message.replace("%member%", name);
        }
        public String setRoom(String room) {
            return this.message.replace("%room%", room);
        }

        public String setMember(Member member) {
            return this.setMember(member.getName());
        }

        public String format(String text) {
            return String.format(this.message, text);
        }
    }

    private String message;
    private Date date;
    private Room room;
    private long expireTime;

    public SystemMessage(Room room, String message, Date date, long expireTime) {
        this.room = room;
        this.message = message;
        this.date = date;
        this.expireTime = expireTime + date.getTime();
    }

    public SystemMessage(Room room, String message, long expireTime) {
        this.room = room;
        this.message = message;
        this.date = new Date();
        this.expireTime = expireTime + this.date.getTime();
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isExpired() {
        return this.date.getTime() > this.expireTime;
    }

    @Override
    public List<String> getMessage(int width) {
        return Utils.splitForWidth(this.getMessage(), width);
    }

    public boolean isExpired(Date joinDate) {
        Main.debug(isExpired() + " " + (joinDate.getTime() > this.date.getTime()));
        return isExpired() && joinDate.getTime() > this.date.getTime();
    }

    @Override
    public boolean isSystem() {
        return false;
    }

    @Override
    public String getRawMessage() {
        return this.message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
