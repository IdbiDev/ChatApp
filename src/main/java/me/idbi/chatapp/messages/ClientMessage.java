package me.idbi.chatapp.messages;

import lombok.Getter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;

import java.io.Serializable;
import java.util.Date;

@Getter
public class ClientMessage implements IMessage, Serializable {

    private static final String chatFormat = "(%s) %s: %s";

    private String sender;
    private Room room;
    private String message;
    private Date date;

    public ClientMessage(String sender, Room room, String message, Date date) {
        this.sender = sender;
        this.room = room;
        this.message = message;
        this.date = date;
    }

    public ClientMessage(String sender, Room room, String message) {
        this.sender = sender;
        this.room = room;
        this.message = message;
        this.date = new Date();
    }

    @Override
    public boolean isSystem() {
        return true;
    }

    @Override
    public String getMessage() {
        return String.format("(%s) %s: %s", Main.getMessageDateFormat().format(date), sender, message);
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
