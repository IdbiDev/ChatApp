package me.idbi.chatapp;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.networking.Client;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.client.DebugMessagePacket;
import me.idbi.chatapp.table.TableManager;
import me.idbi.chatapp.utils.InputManager;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.ViewManager;
import me.idbi.chatapp.view.ViewType;
import me.idbi.chatapp.view.viewmenus.RoomChatView;

import java.io.IOException;
import java.util.*;

@Getter
public class ClientData {

    private int scrollState;
    private Room currentRoom;

    private final Map<UUID, String> passwords;
    @Setter private Map<UUID, Room> rooms;

    private final InputManager inputManager;
    private final TableManager tableManager;
    private final TerminalManager terminalManager;
    private final ViewManager viewManager;
    @Setter private boolean refreshChatRoom;
    private Date joinedDate;

    public ClientData() throws IOException {
        this.scrollState = 0;
        this.rooms = new HashMap<>();
        this.passwords = new HashMap<>();
        this.currentRoom = null;
        this.refreshChatRoom = false;
        this.inputManager = new InputManager();
        this.terminalManager = new TerminalManager();
        this.tableManager = new TableManager();
        this.viewManager = new ViewManager();
    }

    public void setScrollState(int state) {
        this.scrollState = Math.max(0, state);
        this.refreshChatRoom = true;
    }

    public void addScrollState(int state) {
        this.scrollState += state;
        this.refreshChatRoom = true;
    }

    public void addScrollState(List<IMessage> messages, int state) {
        this.scrollState += state;
        //Main.getClient().sendPacket(new DebugMessagePacket(messages.size() + " " + ((RoomChatView) ViewType.ROOM_CHAT.getView()).getScrollMessagesSplitted(messages).size()));
        this.scrollState = Math.min(this.scrollState, ((RoomChatView) ViewType.ROOM_CHAT.getView()).getMessages(messages).size() / Main.getMessagePerScroll());
        this.refreshChatRoom = true;
    }

    public void refreshState(int previousWidth) {
        if(this.viewManager.getCurrentView() instanceof RoomChatView view) {
            int width = Main.getClientData().getTerminalManager().getWidth();
            // ha az előző widthtel a state
            List<String> previousMessages = ((RoomChatView) ViewType.ROOM_CHAT.getView())
                    .getMessages(Main.getClientData().getCurrentRoom().getMessages(), previousWidth);
            List<String> currentMessages = ((RoomChatView) ViewType.ROOM_CHAT.getView())
                    .getMessages(Main.getClientData().getCurrentRoom().getMessages(), width);
            Main.getClient().sendPacket(new DebugMessagePacket(previousMessages.size() + " " + currentMessages.size()));
            int diff = Math.abs(previousMessages.size() - currentMessages.size()) / Main.getMessagePerScroll();
            if(previousWidth < width) {
                this.scrollState -= diff;
            } else { // width < previousWidth
                this.scrollState += diff;
            }
            this.refreshChatRoom = true;
        }
    }

    public void removeScrollState(int state) {
        this.scrollState = Math.max(0, this.scrollState - state);
        this.refreshChatRoom = true;
    }

    public void setCurrentRoom(Room room, Date joinedDate) {
        this.currentRoom = room;
        this.joinedDate = joinedDate;
    }

    public void load() {

    }

    public void save() {

    }
}