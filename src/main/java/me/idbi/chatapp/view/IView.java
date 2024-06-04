package me.idbi.chatapp.view;

import me.idbi.chatapp.networking.Room;

import java.util.List;

public interface IView {

    public static interface Tableable {

    }

    boolean isCursor();
    boolean hasThread();
    boolean hasInput();
    long getUpdateInterval();
    void start();
    void update();
}
