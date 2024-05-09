package me.idbi.chatapp.view;

import me.idbi.chatapp.networking.Room;

public interface IView {

    public static interface Tableable {

    }

    boolean isCursor();
    void show();
}
