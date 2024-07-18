package me.idbi.chatapp.utils;

import dorkbox.util.Sys;
import lombok.Getter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.networkside.Client;
import me.idbi.chatapp.networkside.Server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static List<String> splitForWidth(String message, int width) {
        List<String> lines = new ArrayList<>();
        try {
            StringBuilder buffer = new StringBuilder();
            for (String s : message.split(" ")) {
                String currentWord = s + " ";
                if(currentWord.length() > width) {
                    String tempText = currentWord;
                    int mentalBreakdownProtection = 0;
                    while(tempText.length() > width - buffer.length() && mentalBreakdownProtection < 256) { // ha maga a szó hosszabb mint a hátralévő
                        buffer.append(tempText, 0, width - buffer.length()); // ha már van bufferben, akkor is csak a widthig töltsük

                        lines.add(buffer.toString().strip()); // buffer megtelt, buffer mentése
                        buffer.setLength(0); // buffer törlés

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
            Main.debug(e.getMessage());
        }
        //Main.debug("9");
        return lines;
    }

    @Client
    @Server
    public static void sortRooms(Member member, List<Room> rooms) {
        List<Room> copyRooms = new ArrayList<>(rooms);
        List<Room> sortRooms = new ArrayList<>();
        List<Room> owningRooms = rooms.stream().filter(r -> r.isOwner(member.getUniqueId())).toList();

        List<Room> tempXD = new ArrayList<>(rooms);
        tempXD.removeAll(owningRooms);
        List<Room> savedPassword = tempXD.stream().filter(r -> {
            if(r.hasPassword()) {
                if (member.getPasswords().containsKey(r.getUniqueId())) {
                    if(member.getPasswords().get(r.getUniqueId()).equals(r.getPassword())) {
                        return true;
                    }
                }
            }
            return false;
        }).toList();

        sortRooms.addAll(owningRooms.stream().sorted(Comparator.comparing(Room::getName)).toList());
        sortRooms.addAll(savedPassword.stream().sorted(Comparator.comparing(Room::getName)).toList());

        copyRooms.removeAll(sortRooms);
        sortRooms.addAll(copyRooms.stream().sorted(Comparator.comparing(Room::getName)).toList());

        rooms.clear();
        rooms.addAll(sortRooms);
    }
}
