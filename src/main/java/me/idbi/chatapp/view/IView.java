package me.idbi.chatapp.view;

public interface IView {

    public static interface Tableable {

    }

    ViewType getType();

    boolean isCursor();

    boolean hasThread();

    boolean hasInput();

    long getUpdateInterval();

    void start();

    void update();
}
