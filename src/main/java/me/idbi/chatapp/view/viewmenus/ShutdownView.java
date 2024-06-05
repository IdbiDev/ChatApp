package me.idbi.chatapp.view.viewmenus;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.IView;
import me.idbi.chatapp.view.ViewType;

public class ShutdownView implements IView {
    private int counter;
    @Override
    public ViewType getType() {
        return ViewType.SERVER_SHUTDOWN;
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
        return 1000;
    }

    @Override
    public void start() {
        Main.getClientData().getTerminalManager().clear();
        Main.getClient().setCanRun(false);
        System.out.println(TerminalManager.Color.RED + "A szerver váratlanul leállt. Kérlek várj vagy keress fel egy adminisztrátort." + TerminalManager.Color.RESET);
        this.counter = 0;
    }

    @Override
    public void update() {
        Main.getClient().connect();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        if(this.counter >= 4) {
            System.out.printf("\rCsatlakozás%s", "   ");
            this.counter = 0;
        }
        System.out.printf("\rCsatlakozás%s", ".".repeat(this.counter));
        this.counter++;
    }
}
