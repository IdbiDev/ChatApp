package me.idbi.chatapp.view.viewmenus;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;

import java.util.concurrent.TimeUnit;

public class LoginView implements IView {

    public LoginView() {

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
        Main.getClientData().getTerminalManager().center("Chatapp+", TerminalManager.Style.BOLD,TerminalManager.Color.CYAN);
        System.out.println();
        Main.getClientData().getTerminalManager().center("Csatlakozás " + Main.getClient().getName() + " névvel.", TerminalManager.Style.BOLD);
        System.out.println();
        if (!Main.getClient().connect()) {
            Main.getClientData().getTerminalManager().center("Sikertelen csatlakozás. :(", TerminalManager.Style.BOLD, TerminalManager.Color.RED);
            try {
                Thread.sleep(3000);
                Main.getClientData().getTerminalManager().clear();
                System.exit(-1);
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void update() {

    }
}
