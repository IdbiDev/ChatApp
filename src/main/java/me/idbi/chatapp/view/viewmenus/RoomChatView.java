package me.idbi.chatapp.view.viewmenus;

import lombok.Getter;
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

    @Override
    public boolean isCursor() {
        return false;
    }

    @Override
    public void show() {
        Main.getClientData().getTerminalManager().clear();
        new Thread(new Client.ClientTester()).start();
        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(!Main.getClientData().isRefreshChatRoom()) continue;
            //Main.getClientData().getTerminalManager().clear();
            int termHeight = Main.getClientData().getTerminalManager().getHeight();

            List<IMessage> clientMessages = new ArrayList<IMessage>(Main.getClientData().getCurrentRoom().getMessages())
                    .stream()
                    .filter(msg -> (msg.isSystem() && !((SystemMessage) msg).isExpired(Main.getClientData().getJoinedDate())) || !msg.isSystem())
                    .toList();

            Main.getClientData().getTerminalManager().clear();
            if(clientMessages.size() < termHeight) {
                clientMessages.stream().map(IMessage::getMessage).forEach(System.out::println);
            } else {
                List<String> currentMessages = getScrollMessages(clientMessages);
                currentMessages.forEach(System.out::println);
            }
            System.out.println("DONE");
            Main.getClientData().setRefreshChatRoom(false);
        }
    }

    public List<String> getScrollMessages(List<IMessage> messages) {
        int termHeight = Main.getClientData().getTerminalManager().getHeight();
        int width = Main.getClientData().getTerminalManager().getWidth();
        int state = Main.getClientData().getScrollState();

        List<String> scrollMessages = new ArrayList<>();
        messages.forEach(message -> scrollMessages.addAll(message.getMessage(width)));

        int idx1 = scrollMessages.size() - state * messagesPerScroll;
        int idx2 = scrollMessages.size() - state * messagesPerScroll - (termHeight - 1); // - term.height

        return scrollMessages.subList(Math.max(idx2, 0), Math.max(idx1, termHeight - 1));
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
