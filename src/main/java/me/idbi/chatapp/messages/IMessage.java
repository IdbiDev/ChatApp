package me.idbi.chatapp.messages;

import me.idbi.chatapp.networking.Room;

import java.util.Date;

public interface IMessage {

    Date getDate();
    boolean isSystem();
    Room getRoom();
    String getMessage();
    void setMessage(String message);

}
