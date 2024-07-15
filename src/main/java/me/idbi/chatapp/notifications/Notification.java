package me.idbi.chatapp.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
@Setter
@Getter
@AllArgsConstructor
public class Notification {

    public static TrayIcon icon;

    @Getter
    @AllArgsConstructor
    public static enum Notifications {
        ROOM_CREATED(new Notification("Sikeres szoba létrehozás", "A szobád létre lett hozva.", NotificationType.INFO)),
        ROOM_IS_FULL(new Notification("Sikertelen csatlakozás", "A szoba tele van.", NotificationType.ERROR)),
        WRONG_RENAME(new Notification("Sikertelen újranevezás", "Nem megfelelő karakterek használtál.", NotificationType.ERROR));

        private final Notification notification;

        public void send() {
            this.notification.send();
        }
    }

    @Getter @AllArgsConstructor
    public static enum NotificationType {
        NONE("none.png", TrayIcon.MessageType.NONE),
        INFO("info.png", TrayIcon.MessageType.INFO),
        WARNING("warning.png", TrayIcon.MessageType.WARNING),
        ERROR("error.png", TrayIcon.MessageType.ERROR);

        private final String icon;
        private final TrayIcon.MessageType type;
    }

    private String title;
    private String description;
    private NotificationType type;

     public void send() {
         icon.displayMessage(this.title, this.description, this.type.type);
     }
}
