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

import java.awt.*;
import java.util.*;
import java.util.List;

public class RoomChatView implements IView {
    @Getter private static final int messagesPerScroll = 3;
    @Getter @Setter
    private static boolean doubleRefresh = false;

    @Override
    public boolean isCursor() {
        return false;
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
        return 20;
    }

    @Override
    public void start() {
        Main.getClientData().getTerminalManager().clear();
        new Thread(new Client.ClientTester()).start();
        Main.getClientData().setRefreshChatRoom(true);
    }

    @Override
    public void update() {
        if(!Main.getClientData().isRefreshChatRoom()) return;
        if(!doubleRefresh) {
            doubleRefresh = true;
        }
        //Main.getClientData().getTerminalManager().clear();
        int termHeight = Main.getClientData().getTerminalManager().getHeight();

        List<IMessage> clientMessages = new ArrayList<IMessage>(Main.getClientData().getCurrentRoom().getMessages())
                .stream()
                .filter(msg -> (msg.isSystem() && !((SystemMessage) msg).isExpired(Main.getClientData().getJoinedDate())) || !msg.isSystem())
                .toList();

        Main.getClientData().getTerminalManager().clear();
        if(clientMessages.size() < termHeight - 1) {
            clientMessages.stream().map(IMessage::getMessage).forEach(System.out::println);
        } else {
            List<String> currentMessages = getScrollMessages(clientMessages);
            currentMessages.forEach(System.out::println);
        }
        //System.out.print("");
        //System.out.println(Main.getClientData().getTerminalManager().getKeyboardListener().getBuffer());
        if(doubleRefresh){
            //Main.getClientData().setRefreshChatRoom(false);
            doubleRefresh = false;
        }
    }

    public List<String> getScrollMessages(List<IMessage> messages) {
        int termHeight = Main.getClientData().getTerminalManager().getHeight();
        int width = Main.getClientData().getTerminalManager().getWidth();
        int state = Main.getClientData().getScrollState();
        int previousWidth = Main.getClientData().getPreviousWidth();

        int idx1 = messages.size() - state * messagesPerScroll;
        int idx2 = messages.size() - state * messagesPerScroll - (Math.max(termHeight - 1, 1)); // - term.height

        // mentalBreakdownProtection
        idx1 = Math.min(Math.max(idx1, termHeight - 1), messages.size());
        idx2 = Math.max(idx2, 0);
        Main.debug("innentől: " + idx2 + " idáig: " + idx1);

        messages = messages.subList(idx2, idx1);

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
        Main.debug(changedLines + " " + (changedLines / Main.getMessagePerScroll()) + " " + Main.getClientData().getScrollState());

        idx1 = scrollMessages.size() - state * messagesPerScroll;
        idx2 = scrollMessages.size() - state * messagesPerScroll - (termHeight - 1); // - term.height

        return scrollMessages.subList(Math.max(idx2, 0), Math.min(Math.max(idx1, termHeight - 1), scrollMessages.size()));
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
