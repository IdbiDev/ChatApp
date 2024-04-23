package me.idbi.chatapp.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.view.viewmenus.GlobalBanView;
import me.idbi.chatapp.view.viewmenus.LoginView;
import me.idbi.chatapp.view.viewmenus.RoomJoinView;
import me.idbi.chatapp.view.viewmenus.RoomListView;

@Getter
@AllArgsConstructor
public enum ViewType {

    LOGIN(new LoginView()),
    ROOM_JOIN(new RoomJoinView()),
    GLOBAL_BAN(new GlobalBanView()),
    ROOM_LIST(new RoomListView());

    private final IView view;
}
