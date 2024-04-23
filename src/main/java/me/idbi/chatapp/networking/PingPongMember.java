package me.idbi.chatapp.networking;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PingPongMember {
    private long lastPing;
    private int failCount;
    PingPongMember() {
        lastPing = 0;
        failCount = 0;
    }
}
