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
    ROOM_LIST(new RoomListView());

    private final IView view;
}
