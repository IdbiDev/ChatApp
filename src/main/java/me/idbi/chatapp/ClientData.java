package me.idbi.chatapp;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.messages.SystemMessage;
import me.idbi.chatapp.networking.Room;
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
    @Setter private int previousWidth;
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
        this.previousWidth = this.terminalManager.getWidth();
    }

    public void setScrollState(int state) {
        this.scrollState = Math.max(0, state);
        this.refreshChatRoom = true;
    }

    public void addScrollState(int state) {
        this.scrollState += state;
        if(this.scrollState < 0)
            this.scrollState = 0;
        this.refreshChatRoom = true;
    }

    public void addScrollState(List<IMessage> messages, int state) {
        this.scrollState += state;
        //Main.getClient().sendPacket(new DebugMessagePacket(messages.size() + " " + ((RoomChatView) ViewType.ROOM_CHAT.getView()).getScrollMessagesSplitted(messages).size()));
        this.scrollState = Math.min(this.scrollState, ((RoomChatView) ViewType.ROOM_CHAT.getView()).getScrollMessages(messages).size() / Main.getMessagePerScroll());
        //this.refreshChatRoom = true;
    }

    public void refreshState(int previousWidth) {
        if(this.viewManager.getView() instanceof RoomChatView view) {
            int width = Main.getClientData().getTerminalManager().getWidth();
            // ha az előző widthtel a state

            List<IMessage> currentMessages = Main.getClientData().getCurrentRoom().getMessages()
                    .stream()
                    .filter(msg -> (msg.isSystem() && !((SystemMessage) msg).isExpired(Main.getClientData().getJoinedDate())) || !msg.isSystem())
                    .toList();
//
            List<IMessage> previousIMessages = view.getScrollIMessages(currentMessages, previousWidth);
            List<IMessage> currentIMessages = view.getScrollIMessages(currentMessages, width);

            int lineChangeCount = 0;
            int counter = 0;
            for (IMessage previousIMessage : previousIMessages) {
                if(previousIMessage.getMessage(previousWidth).size() != currentIMessages.get(counter).getMessage(width).size()) {
                    int diff = (previousIMessage.getMessage(previousWidth).size() - currentIMessages.get(counter).getMessage(width).size()) / Main.getScrollState();
                    if(diff < 0) {
                        this.scrollState -= diff;
                    } else {
                        this.scrollState += diff;
                    }
                }
                counter++;
            }


           // Main.getClient().sendPacket(new DebugMessagePacket(previousMessages.size() + " " + currentMessages.size()));

            this.refreshChatRoom = true;
        }
    }

    public void removeScrollState(int state) {
        this.scrollState -= state;
        if(this.scrollState < 0)
            this.scrollState = 0;
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