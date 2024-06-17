package me.idbi.chatapp.view.viewmenus;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.ClientData;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.messages.ClientMessage;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.messages.SystemMessage;
import me.idbi.chatapp.networking.Client;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.client.DebugMessagePacket;
import me.idbi.chatapp.packets.client.SendMessageToServerPacket;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RoomChatView implements IView {
    @Getter private static final int messagesPerScroll = 3;
    @Getter @Setter private static boolean doubleRefresh = false;

    @Override
    public ViewType getType() {
        return ViewType.ROOM_CHAT;
    }

    @Override
    public boolean isCursor() {
        return true;
    }

    @Override
    public boolean hasThread() {
        return true;
    }

    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public long getUpdateInterval() {
        return 10;
    }

    @Override
    public void start() {
        Main.getClientData().getTerminalManager().clear();
        //new Thread(new Client.ClientTester()).start();
        Main.getClientData().setRefreshChatRoom(true);
        Main.getClientData().refreshBuffer();

    }

    @Override
    public void update() {
        for (int i = 0; i <= 1; i++) {
            if(Main.getClientData().isRefreshChatRoom()) {
                int termHeight = Main.getClientData().getTerminalManager().getHeight();
                Main.getClientData().getTerminalManager().home();

                List<IMessage> clientMessages = new ArrayList<IMessage>(Main.getClientData().getCurrentRoom().getMessages())
                        .stream()
                        .filter(msg -> (msg.isSystem() && !((SystemMessage) msg).isExpired(Main.getClientData().getJoinedDate())) || !msg.isSystem())
                        .toList();

                Main.getClientData().getTerminalManager().clear();
//            if(clientMessages.size() < termHeight - 1) {
//                clientMessages.stream().map(IMessage::getMessage).forEach(System.out::println);
//                // clientMessages.add(Main.getClient().getName() + " > " + Main.getClientData().getTerminalManager().getKeyboardListener().getBuffer());
//            } else {
                List<String> currentMessages = getScrollMessages(clientMessages);
                currentMessages.forEach(System.out::println);

            }

            // }
        }
        if(Main.getClientData().isRefreshBuffer()) {
            Main.getClientData().getTerminalManager().moveCursor(Main.getClientData().getTerminalManager().getHeight(),0);
            System.out.print(Main.getClient().getName() + " > " + Main.getClientData().getTerminalManager().getKeyboardListener().getBuffer());
            Main.getClientData().setRefreshBuffer(false);
        }
        Main.getClientData().setRefreshChatRoom(false);

        //Main.getClientData().getTerminalManager().clear();

    }

    public List<String> getScrollMessages(List<IMessage> messages) {
        int termHeight = Main.getClientData().getTerminalManager().getHeight();
        int width = Main.getClientData().getTerminalManager().getWidth();
        int previousWidth = Main.getClientData().getPreviousWidth();

        int changedLines = 0;
        List<String> scrollMessages = new ArrayList<>();
        for (IMessage message : messages) {
            List<String> splitted = message.getMessage(width);
            List<String> previousSplitted = message.getMessage(previousWidth);

            //                  2                   1
            changedLines += splitted.size() - previousSplitted.size();

            scrollMessages.addAll(splitted);
        }

        Main.getClientData().addScrollState(changedLines / Main.getMessagePerScroll());

//        int idx1 = messages.size() - state * messagesPerScroll;
//        int idx2 = messages.size() - state * messagesPerScroll - (Math.max(termHeight - 1, 1)); // - term.height
//
//        // mentalBreakdownProtection
//        idx1 = Math.min(Math.max(idx1, termHeight - 1), messages.size());
//        idx2 = Math.max(idx2, 0);


       // messages = messages.subList(idx2, idx1);

        int state = Main.getClientData().getScrollState();
        int scrolling = state * messagesPerScroll;
        // Main.debug("size: " + scrollMessages.size() + " stateMsgs: " + scrolling + " termHeight:" + Math.max(termHeight - 1, 1));
        int idx1 = scrollMessages.size() - scrolling;
        int idx2 = (scrollMessages.size() - scrolling) - (Math.max(termHeight - 1, 1)); // - term.height

        idx1 = Math.min(Math.max(idx1, termHeight - 1), messages.size());
        idx2 = Math.max(idx2, 0);

        //Main.debug("innentől: " + idx2 + " idáig: " + idx1 + " state: " + state);
        //Main.debug(changedLines + " " + (changedLines / Main.getMessagePerScroll()) + " " + Main.getClientData().getScrollState());

        return scrollMessages.subList(idx2, idx1);
    }

    public List<IMessage> getScrollIMessages(List<IMessage> messages, int width) {

        int termHeight = Main.getClientData().getTerminalManager().getHeight();
        int state = Main.getClientData().getScrollState();

        int idx1 = messages.size() - state * messagesPerScroll;
        int idx2 = messages.size() - state * messagesPerScroll - (termHeight - 1); // - term.height

        List<IMessage> subbedIMessages = messages.subList(Math.max(idx2, 0), Math.max(idx1, termHeight - 1));

        Map<Integer, List<String>> idk = new HashMap<>();
        Map<Integer, IMessage> imessages = new HashMap<>();

        subbedIMessages.forEach(message -> idk.put(idk.size(), message.getMessage(width)));
        subbedIMessages.forEach(message -> imessages.put(imessages.size(), message));

        int i1 = idk.size() - state * messagesPerScroll;
        int i2 = idk.size() - state * messagesPerScroll - (termHeight - 1); // - term.height

        List<IMessage> asdXDDD = new ArrayList<>();

        for(int i = i2; i < i1; i++) {
            asdXDDD.add(imessages.get(i));
        }

        return asdXDDD;
        //List<String> buziGeci = new ArrayList<>();
        //asdXDDD.forEach(message -> buziGeci.addAll(message.getMessage(width)));
        //return buziGeci.subList(0, Math.min(termHeight - 1, buziGeci.size()));
        //return messages.subList(Math.max(idx2, 0), Collections.max(Arrays.asList(idx1, termHeight - 1, messages.size())));
    }
    public List<String> getScrollMessagesSplitted(List<IMessage> messages) {
        List<String> asd = new ArrayList<>();

        int messageCount = messages.size();
        int counter = 0;
        while (asd.size() < messageCount) {
           // asd.addAll(getLines(messages.get(counter)));
            counter++;
        }

        Collections.reverse(asd);
        return asd.subList(0, messageCount);
    }

}
