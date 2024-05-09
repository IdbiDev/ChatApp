package me.idbi.chatapp.view.viewmenus;

import lombok.Getter;
import me.idbi.chatapp.ClientData;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.messages.ClientMessage;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.messages.SystemMessage;
import me.idbi.chatapp.networking.Client;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.packets.client.SendMessageToServerPacket;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.IView;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(!Main.getClientData().isRefreshChatRoom()) continue;
            //Main.getClientData().getTerminalManager().clear();
            int termHeight = Main.getClientData().getTerminalManager().getHeight();

            List<IMessage> clientMessages = Main.getClientData().getCurrentRoom().getMessages()
                    .stream()
                    .filter(msg -> (msg.isSystem() && !((SystemMessage) msg).isExpired(Main.getClientData().getJoinedDate())) || !msg.isSystem())
                    .toList();

            Main.getClientData().getTerminalManager().clear();
            if(clientMessages.size() < termHeight) {
                clientMessages.stream().map(IMessage::getMessage).forEach(System.out::println);
            } else {
                List<String> currentMessages = getScrollMessages(clientMessages, Main.getClientData().getScrollState());
                currentMessages.forEach(System.out::println);
            }
            Main.getClientData().setRefreshChatRoom(false);
        }
    }

    public List<String> getScrollMessages(List<IMessage> messages, int state) {
        int termHeight = Main.getClientData().getTerminalManager().getHeight();
        List<String> scrollMessages = getMessages(messages);
        int idx1 = scrollMessages.size() - state * messagesPerScroll;
        int idx2 = scrollMessages.size() - state * messagesPerScroll - (termHeight - 1); // - term.height

        return scrollMessages.subList(Math.max(idx2, 0), Math.max(idx1, termHeight - 1));
    }

    public List<String> getScrollMessagesSplitted(List<IMessage> messages) {
        List<String> asd = new ArrayList<>();

        int messageCount = messages.size();
        int counter = 0;
        while (asd.size() < messageCount) {
            asd.addAll(getLines(messages.get(counter)));
            counter++;
        }

        Collections.reverse(asd);
        return asd.subList(0, messageCount);
    }

    public List<String> getMessages(List<IMessage> messages) {
        List<String> asd = new ArrayList<>();

        for (IMessage message : messages) {
            asd.addAll(getLines(message));
        }

        return asd;
    }

    public List<String> getMessages(List<IMessage> messages, int width) {
        List<String> asd = new ArrayList<>();

        for (IMessage message : messages) {
            asd.addAll(getLines(message, width));
        }

        return asd;
    }

    private List<String> getLines(IMessage message, int width) {
        List<String> lines = new ArrayList<>();
        try {
            StringBuilder buffer = new StringBuilder();
            for (String s : message.getMessage().split(" ")) {
                String currentWord = s + " ";
                if(currentWord.length() > width) {
                    String tempText = currentWord;
                    int mentalBreakdownProtection = 0;
                    while(tempText.length() > width - buffer.length() && mentalBreakdownProtection < 256) { // ha maga a szó hosszabb mint a hátralévő
                        buffer.append(tempText.substring(0, width - buffer.length())); // ha már van bufferben, akkor is csak a widthig töltsük

                        lines.add(buffer.toString().strip()); // buffer megtelt, buffer mentése
                        buffer.setLength(0); // buffer törlése

                        tempText = tempText.substring(width); // már levágott részt, kövi loopra kivágjuk
                        mentalBreakdownProtection++;
                    }

                    buffer.append(tempText);
                    continue;
                } else if(currentWord.length() + buffer.length() <= width) {
                    buffer.append(currentWord);
                    continue;
                }

                lines.add(buffer.toString().strip());
                buffer.setLength(0);
                buffer.append(currentWord);
            }
            lines.add(buffer.toString().strip());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

    private List<String> getLines(IMessage message) {
        int width = Main.getClientData().getTerminalManager().getWidth();
        return getLines(message, width);
    }
}
