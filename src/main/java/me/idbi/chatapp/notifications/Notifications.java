package me.idbi.chatapp.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Notifications {
    NO_PERMISSION(new Notification("A művelet meghiúsult", "Ehhez nincs jogod!", Notification.NotificationType.ERROR)),
    WRONG_TEXT_INPUT(new Notification("Helytelen szöveg bemenet", null, Notification.NotificationType.ERROR)),
    COMMAND_USAGE(new Notification("Rossz parancs használat", "Használd: %s", Notification.NotificationType.ERROR)),
    TARGET_NOT_FOUND(new Notification("Nem található felhasználó", "A megadott névvel nem található felhasználó a szobában.", Notification.NotificationType.ERROR)),

    MENTION(new Notification("Említetés", "%s megemlített az üzenetében!", Notification.NotificationType.WARNING)),

    // room join
    ROOM_IS_FULL(new Notification("Sikertelen csatlakozás", "A szoba megtelt.", Notification.NotificationType.ERROR)),
    INVALID_ROOM(new Notification("Sikertelen csatlakozás", "Ismeretlen szoba.", Notification.NotificationType.ERROR)),
    ROOM_JOIN_CANCELLED(new Notification("Sikertelen csatlakozás", "Ismeretlen hiba.", Notification.NotificationType.ERROR)),

    // room manage
    ROOM_DISBANDED_OWNER(new Notification("Szoba törlés", "A szobád törölve lett.", Notification.NotificationType.INFO)),
    ROOM_DISBANDED_MEMBERS(new Notification("Szoba törlés", "A szoba amiben voltál törölve lett.", Notification.NotificationType.INFO)),

    ROOM_SETOWNER_SENDER(new Notification("Sikeres tulajdonjog átruházás", "A szobád tulajdonjogát átruháztad egy másik felhasználóra.", Notification.NotificationType.INFO)),
    ROOM_SETOWNER_TARGET(new Notification("Sikeres tulajdonjog átruházás", "A %s nevű szoba tulajdonjogát átruházták rád.", Notification.NotificationType.INFO)),

    ROOM_CREATED(new Notification("Sikeres szoba létrehozás", "A szobád létre lett hozva.", Notification.NotificationType.INFO)),
    ROOM_PASSWORD_CHANGED(new Notification("Sikeres jelszó változtatás", "Az új jelszó: %s", Notification.NotificationType.INFO)),
    ROOM_RENAMED(new Notification("Sikeres újranevezés", "Sikeresen átnevezted a szobádat.", Notification.NotificationType.INFO)),
    ROOM_ALREADY_EXISTS(new Notification("Sikertelen létrehozása", "Ilyen nevű szoba már létezik.", Notification.NotificationType.ERROR)),
    ROOM_CREATE_TOO_MANY_ROOMS(new Notification("Sikertelen szoba létrehozás", "Túl sok szoba van a tulajdonodban.", Notification.NotificationType.ERROR)),

    // permanent
    ROOM_PERMANENT_ON(new Notification("Örök szoba", "A szobád mostantól nem törlődik.", Notification.NotificationType.INFO)),
    ROOM_PERMANENT_OFF(new Notification("Ideiglenes szoba", "Mostantól szobád törlődik inaktivitás esetén.", Notification.NotificationType.WARNING)),

    ;

    private final Notification notification;

    public void send() {
        this.notification.send();
    }
}
