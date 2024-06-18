package me.idbi.chatapp.utils;

import me.idbi.chatapp.Main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.function.Consumer;

public class InputManager {

    public void getInput(String text, Consumer<String> consumer, Runnable exit) {
        Main.getClientData().getTerminalManager().getKeyboardListener().setInputMode(true);
        Main.getClientData().getTerminalManager().getKeyboardListener().setInputPrompt("");

        Main.getClientData().getTerminalManager().clearLine();
        while(Main.getClientData().getTerminalManager().getKeyboardListener().isInputMode()) {
            if(Main.getClientData().getTerminalManager().getKeyboardListener().isPrepareExit()) break;
            System.out.print("\r" + text + Main.getClientData().getTerminalManager().getKeyboardListener().getInputBuffer());
        }

        // keyboard listener
        if(Main.getClientData().getTerminalManager().getKeyboardListener().isPrepareExit()) {
            //exit
            Main.getClientData().getTerminalManager().getKeyboardListener().setPrepareExit(false);
            exit.run();
        } else {
            //Accept
            String buffer = Main.getClientData().getTerminalManager().getKeyboardListener().getInputBuffer().strip();
            consumer.accept(buffer.isBlank() ? null : buffer.strip());
        }
        Main.getClientData().getTerminalManager().getKeyboardListener().setInputBuffer("");
    }
}