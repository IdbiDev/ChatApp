package me.idbi.chatapp.utils;

import me.idbi.chatapp.Main;

import java.util.Scanner;

public class InputManager {

    public InputManager() {

    }

    public String getInput(String text) {
        System.out.print(text);
        Scanner scn = new Scanner(System.in);

        return scn.hasNextLine() ? scn.nextLine() : null;
    }
}
