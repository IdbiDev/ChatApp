package me.idbi.chatapp.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
public class Notification implements Serializable {

    @Setter
    @Getter
    private static TrayIcon icon;

    public static void load() {
        try {
            Image image = Toolkit.getDefaultToolkit().createImage("info.png");
            icon = new TrayIcon(image);
            icon.setImageAutoSize(true);
            SystemTray.getSystemTray().add(icon);
            Notification.setIcon(icon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Getter
    @AllArgsConstructor
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
