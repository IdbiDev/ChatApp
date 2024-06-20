package me.idbi.chatapp.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.view.viewmenus.*;

@Getter
@AllArgsConstructor
public enum ViewType {

    LOGIN(new LoginView()),
    ROOM_JOIN(new RoomJoinView()),
    GLOBAL_BAN(new GlobalBanView()),
    ROOM_CHAT(new RoomChatView()),
    SERVER_SHUTDOWN(new ShutdownView()),
    ROOM_CREATE(new RoomCreateView()),
    ROOM_CREATE_CONFIRM(new RoomCreateConfirmView()),
    ROOM_LIST(new RoomListView());

    private final IView view;
}
