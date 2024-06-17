package me.idbi.chatapp.messages;

import me.idbi.chatapp.networking.Room;

import java.util.Date;
import java.util.List;

public interface IMessage {

    Date getDate();
    void setDate(Date date);
    boolean isSystem();
    Room getRoom();
    String getMessage();
    List<String> getMessage(int width);
    void setMessage(String message);

}
