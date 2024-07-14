package me.idbi.chatapp.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

@Getter
@AllArgsConstructor
public class Notification {

    public static TrayIcon icon;

    public static enum Notifications {
        ROOM_IS_FULL();
    }

    @Getter @AllArgsConstructor
    public static enum NotificationType {
        NONE("none.png", TrayIcon.MessageType.NONE),
        INFO("info.png", TrayIcon.MessageType.INFO),
        WARNING("warning.png", TrayIcon.MessageType.WARNING),
        ERROR("error.png", TrayIcon.MessageType.ERROR);

        private String icon;
        private TrayIcon.MessageType type;
    }

    private String title;
    private String description;
    private NotificationType type;

     public void send() throws AWTException {
         //Obtain only one instance of the SystemTray object
         SystemTray tray = SystemTray.getSystemTray();

         // If you want to create an icon in the system tray to preview
         Image image = Toolkit.getDefaultToolkit().createImage("info.png");
         //Alternative (if the icon is on the classpath):

//         TrayIcon trayIcon = icon;
//         //Let the system resize the image if needed
//         trayIcon.setImageAutoSize(true);
//         //Set tooltip text for the tray icon
//         trayIcon.setToolTip("22222");
//         //tray.add(trayIcon);

         // Display info notification:
         icon.displayMessage(this.title, this.description, MessageType.ERROR);
     }
}
