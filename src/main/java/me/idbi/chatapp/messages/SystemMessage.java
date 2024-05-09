package me.idbi.chatapp.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;

import java.io.Serializable;
import java.util.Date;

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

    public SystemMessage(Room room, String message, Date date) {
        this.room = room;
        this.message = message;
        this.date = date;
    }

    public SystemMessage(Room room, String message) {
        this.room = room;
        this.message = message;
        this.date = new Date();
    }

    @Override
    public boolean isSystem() {
        return false;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}