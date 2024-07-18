package me.idbi.chatapp.networkside;

public class IllegalNetworkSideException extends RuntimeException {

    public IllegalNetworkSideException() {
    }

    public IllegalNetworkSideException(String message) {
        super(message);
    }
}
