package me.idbi.chatapp.eventmanagers.interfaces;

public interface Cancellable {

    public boolean isCancelled();
    public void setCancelled(boolean cancel);
}
