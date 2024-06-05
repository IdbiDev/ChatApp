package me.idbi.chatapp.view;

import lombok.Getter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.utils.TerminalManager;

import java.io.IOException;

@Getter
public class ViewManager {

    private IView view;
    private Thread thread;
    private boolean refresh;

    public ViewManager() {
        this.view = null;
        this.thread = null;
    }

    public ViewManager(IView view) {
        setView(view);
    }

    public void setView(IView view) {
        this.view = view;

        if (view.isCursor()) Main.getClientData().getTerminalManager().showCursor();
        else Main.getClientData().getTerminalManager().hideCursor();

        if (this.thread != null && this.thread.isAlive()) {
            this.thread.interrupt();
            this.thread = null;
        }

        if (view.hasThread()) {
            this.thread = new Thread(this::startUpdater);
            this.thread.start();
            return;
        }
        startUpdater();
    }

    public void setView(ViewType type) {
        setView(type.getView());
    }

    public void startUpdater() {
        TerminalManager manager = Main.getClientData().getTerminalManager();
        manager.setCanWrite(this.view.hasInput());
        if(this.view.isCursor()) {
            manager.showCursor();
        } else {
            manager.hideCursor();
        }

        this.view.start();
        while(this.view != null) {
            if(this.view.getUpdateInterval() == -1) break;
            this.view.update();
            try {
                Thread.sleep(this.view.getUpdateInterval());
            } catch (InterruptedException ignored) {

            }
        }
    }

    public void refresh() {
        Main.getClientData().getTerminalManager().clear();
        if(this.view != null) {
            this.view.update();
        }
    }

    public void clearView() {
        this.view = null;
        if(this.thread != null && this.thread.isAlive()) {
            this.thread.interrupt();
            this.thread = null;
        }
    }

//    public void changeView(ViewType view) {
//        this.changeView(view.getView());
//    }
//
//    public void changeView(IView view) {
//       /* if(this.currentThread != null && this.currentThread.isAlive())
//            this.currentThread.interrupt();*/
//
//        this.currentView = view;
//        if(view.isCursor())
//            Main.getClientData().getTerminalManager().showCursor();
//        else
//            Main.getClientData().getTerminalManager().hideCursor();
////        this.currentThread = new Thread(view::show);
////        this.currentThread.start();
//        Main.getClientData().setScrollState(0);
//        view.show();
//    }
//    public void threadedView(ViewType view) {
//        ThreadView t = new ThreadView(view);
//        new Thread(t).start();
//    }
//
//    public void exitView() {
//        this.currentView = null;
//        Main.getClientData().getTerminalManager().showCursor();
//       /* if(this.currentThread != null && this.currentThread.isAlive())
//            this.currentThread.interrupt();*/
//
//    }
//
//    public void refresh() {
//        if(this.currentView == null) return;
//        changeView(this.currentView);
//    }
//
//    public static class ThreadView implements Runnable {
//        private final ViewType view;
//        public ThreadView(ViewType view) {
//            this.view = view;
//        }
//        @Override
//        public void run() {
//            Main.getClientData().getViewManager().changeView(this.view);
//        }
//    }
}
