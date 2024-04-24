package me.idbi.chatapp.view.viewmenus;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.IView;

public class GlobalBanView implements IView {
    @Override
    public boolean isCursor() {
        return false;
    }

    @Override
    public void show() {
        Main.getTerminalManager().setBackgroundColor(TerminalManager.Color.BLUE_BACKGROUND);
        System.out.println("Chatapp+\n\n\n\n\n");

        System.out.println(
                """
                \t\t\t 
                              ████
                           ████
                ███      ████
                        ███
                        ███
                ███      ████
                           ████
                              ████
                """
                );
        System.out.println("Ki lettél tiltva az appból! Mégis mit csináltál????????");
    }
}
