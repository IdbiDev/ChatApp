package me.idbi.chatapp.view.viewmenus;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;

public class GlobalBanView implements IView {
    @Override
    public ViewType getType() {
        return ViewType.GLOBAL_BAN;
    }

    @Override
    public boolean isCursor() {
        return false;
    }

    @Override
    public boolean hasThread() {
        return false;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public long getUpdateInterval() {
        return -1;
    }

    @Override
    public void start() {
        Main.getClientData().getTerminalManager().setBackgroundColor(TerminalManager.Color.BLUE_BACKGROUND);
        System.out.println("Chatapp+\n\n\n\n\n");

        System.out.println(
                """
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
        System.out.println("Ki lettél tiltva!");
    }

    @Override
    public void update() {}
}
