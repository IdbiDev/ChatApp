package me.idbi.chatapp.utils;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.messages.IMessage;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<String> splitForWidth(String message, int width) {
        List<String> lines = new ArrayList<>();
        try {
            //Main.debug("1");
            StringBuilder buffer = new StringBuilder();
            for (String s : message.split(" ")) {
                //Main.debug("2");
                String currentWord = s + " ";
                if(currentWord.length() > width) {
                    //Main.debug("3");
                    String tempText = currentWord;
                    int mentalBreakdownProtection = 0;
                    while(tempText.length() > width - buffer.length() && mentalBreakdownProtection < 256) { // ha maga a szó hosszabb mint a hátralévő
                        //Main.debug("4");
                        buffer.append(tempText.substring(0, width - buffer.length())); // ha már van bufferben, akkor is csak a widthig töltsük

                        lines.add(buffer.toString().strip()); // buffer megtelt, buffer mentése
                        buffer.setLength(0); // buffer törlése
                        //Main.debug("4.1");
                        tempText = tempText.substring(width); // már levágott részt, kövi loopra kivágjuk
                        mentalBreakdownProtection++;
                    }

                    buffer.append(tempText);
                    //Main.debug("5");
                    continue;
                } else if(currentWord.length() + buffer.length() <= width) {
                    //Main.debug("6");
                    buffer.append(currentWord);
                    continue;
                }

                //Main.debug("7");
                lines.add(buffer.toString().strip());
                buffer.setLength(0);
                buffer.append(currentWord);
                //Main.debug("7.1");
            }
            //Main.debug("8");
            lines.add(buffer.toString().strip());
        } catch (Exception e) {
            Main.debug(e.getMessage());
        }
        //Main.debug("9");
        return lines;
    }
}
