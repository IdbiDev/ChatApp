package me.idbi.chatapp.packets.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.notifications.Notification;
import me.idbi.chatapp.notifications.Notifications;
import me.idbi.chatapp.packets.ServerPacket;

@Getter
public class SendNotificationPacket extends ServerPacket {
    private final Notification notification;

    public SendNotificationPacket(Notifications notifications) {
        this.notification = notifications.getNotification();
    }

    public SendNotificationPacket(Notification notification) {
        this.notification = notification;
    }
}
