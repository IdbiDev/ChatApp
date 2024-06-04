package me.idbi.chatapp.utils;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.messages.IMessage;

import java.util.ArrayList;
import java.util.List;

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
}