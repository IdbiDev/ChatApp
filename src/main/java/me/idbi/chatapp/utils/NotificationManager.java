package me.idbi.chatapp.utils;

import dorkbox.notify.Notify;
import dorkbox.notify.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

public class NotificationManager {

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

    public NotificationManager() {

    }

    public void info(String title, String description) {
        Notify.Companion.create().title(title).text(description).showInformation();
    }

//    public void displayTray(String title, String description, NotificationType type) {
//        try {
//            //Obtain only one instance of the SystemTray object
//            SystemTray tray = SystemTray.getSystemTray();
//
//            //If the icon is a file
//            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
//            //Alternative (if the icon is on the classpath):
//            //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));
//
//            TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
//            //Let the system resize the image if needed
//            trayIcon.setImageAutoSize(true);
//            //Set tooltip text for the tray icon
//            trayIcon.setToolTip("System tray icon demo");
//            tray.add(trayIcon);
//
//            trayIcon.displayMessage("Hello, World", "notification demo", TrayIcon.MessageType.);
//        } catch (AWTException e) {
//            e.printStackTrace();
//        }
//    }
}

