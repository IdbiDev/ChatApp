package me.idbi.chatapp.networking;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class PingPongMember {
    private long lastPing = 0;
    private int failCount = 0;
}
