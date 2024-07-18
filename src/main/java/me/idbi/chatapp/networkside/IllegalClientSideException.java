package me.idbi.chatapp.networkside;

public class IllegalClientSideException extends IllegalNetworkSideException {
    public IllegalClientSideException() {
        super("This function is only callable from client-side");
    }
    public IllegalClientSideException(String message) {
        super(message);
    }
}
