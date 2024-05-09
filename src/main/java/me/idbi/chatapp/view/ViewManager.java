package me.idbi.chatapp.view;

import lombok.Getter;
import me.idbi.chatapp.Main;

@Getter
public class ViewManager {

    private IView currentView;
    //private Thread currentThread;

    public ViewManager() {
        this.currentView = null;
        //this.currentThread = null;
    }

    public void changeView(ViewType view) {
        this.changeView(view.getView());
    }

    public void changeView(IView view) {
       /* if(this.currentThread != null && this.currentThread.isAlive())
            this.currentThread.interrupt();*/

        this.currentView = view;
        if(view.isCursor())
            Main.getClientData().getTerminalManager().showCursor();
        else
            Main.getClientData().getTerminalManager().hideCursor();
//        this.currentThread = new Thread(view::show);
//        this.currentThread.start();
        Main.getClientData().setScrollState(0);
        view.show();
    }
    public void threadedView(ViewType view) {
        ThreadView t = new ThreadView(view);
        new Thread(t).start();
    }

    public void exitView() {
        this.currentView = null;
        Main.getClientData().getTerminalManager().showCursor();
       /* if(this.currentThread != null && this.currentThread.isAlive())
            this.currentThread.interrupt();*/

    }

    public void refresh() {
        if(this.currentView == null) return;
        changeView(this.currentView);
    }

    public static class ThreadView implements Runnable {
        private final ViewType view;
        public ThreadView(ViewType view) {
            this.view = view;
        }
        @Override
        public void run() {
            Main.getClientData().getViewManager().changeView(this.view);
        }
    }
}
