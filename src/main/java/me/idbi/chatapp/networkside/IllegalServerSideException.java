package me.idbi.chatapp.networkside;

public class IllegalServerSideException extends IllegalNetworkSideException {
    public IllegalServerSideException(String message) {
        super(message);
    }
    public IllegalServerSideException() {
        super("This function is only callable from server-side");
    }
}
