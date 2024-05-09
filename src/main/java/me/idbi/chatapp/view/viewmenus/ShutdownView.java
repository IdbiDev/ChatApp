package me.idbi.chatapp.view.viewmenus;

import me.idbi.chatapp.Main;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.IView;

public class ShutdownView implements IView {

    @Override
    public boolean isCursor() {
        return false;
    }

    @Override
    public void show() {
        Main.getClientData().getTerminalManager().clear();
        Main.getClient().setCanRun(false);
        System.out.println(TerminalManager.Color.RED + "A szerver váratlanul leállt. Kérlek várj vagy keress fel egy adminisztrátort." + TerminalManager.Color.RESET);
        int counter = 0;
        while (!Main.getClient().connect()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(counter >= 4) {
                System.out.printf("\rCsatlakozás%s", "   ");
                counter = 0;
            }
            System.out.printf("\rCsatlakozás%s", ".".repeat(counter));
            counter++;
        }
    }
}
