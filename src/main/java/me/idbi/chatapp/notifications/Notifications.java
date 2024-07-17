package me.idbi.chatapp.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Notifications {
    NO_PERMISSION(new Notification("A művelet meghiúsult", "Ehhez nincs jogod!", Notification.NotificationType.ERROR)),
    COMMAND_USAGE(new Notification("Rossz parancs használat", "Használd: %s", Notification.NotificationType.ERROR)),
    WRONG_CHARACTER(new Notification("Helytelen karakter használat", null, Notification.NotificationType.ERROR)),
    ROOM_CREATED(new Notification("Sikeres szoba létrehozás", "A szobád létre lett hozva.", Notification.NotificationType.INFO)),
    ROOM_ALREADY_EXISTS(new Notification("Sikertelen létrehozása", "Ilyen nevű szoba már létezik.", Notification.NotificationType.ERROR)),
    ROOM_IS_FULL(new Notification("Sikertelen csatlakozás", "A szoba megtelt.", Notification.NotificationType.ERROR)),
    INVALID_ROOM(new Notification("Sikertelen csatlakozás", "Ismeretlen szoba.", Notification.NotificationType.ERROR)),
    ROOM_JOIN_CANCELLED(new Notification("Sikertelen csatlakozás", "Ismeretlen hiba.", Notification.NotificationType.ERROR)),
    ROOM_PASSWORD_CHANGED(new Notification("Sikeres jelszó változtatás", "Az új jelszó: %s", Notification.NotificationType.INFO)),
    ROOM_RENAMED(new Notification("Sikeres újranevezés", "Sikeresen átnevezted a szobádat.", Notification.NotificationType.INFO));
    //WRONG_RENAME(new Notification("Sikertelen újranevezás", "Nem megfelelő karaktereket használtál.", Notification.NotificationType.ERROR));

    private final Notification notification;

    public void send() {
        this.notification.send();
    }
}
