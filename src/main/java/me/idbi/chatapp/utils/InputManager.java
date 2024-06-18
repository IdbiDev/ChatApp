package me.idbi.chatapp.utils;

import me.idbi.chatapp.Main;

import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.function.Consumer;

public class InputManager {

    public void getInput(String text, Consumer<String> consumer, Runnable exit) {
        Main.getClientData().getTerminalManager().getInputMode().set(true);
        Main.getClientData().getTerminalManager().getKeyboardListener().setInputPrompt("");

        Main.debug(Main.getClientData().getTerminalManager().getInputMode().get() + "");

        while(Main.getClientData().getTerminalManager().getInputMode().get()) {
            System.out.println("\r" + text + Main.getClientData().getTerminalManager().getKeyboardListener().getInputBuffer().get());
        }

        // keyboard listener
        if(Main.getClientData().getTerminalManager().getKeyboardListener().getInputBuffer() == null) {
            //exit
            exit.run();
        }else {
            //Accept
            consumer.accept(Main.getClientData().getTerminalManager().getKeyboardListener().getInputBuffer().get());
        }
        Main.getClientData().getTerminalManager().getKeyboardListener().getInputBuffer().set("");
    }
}