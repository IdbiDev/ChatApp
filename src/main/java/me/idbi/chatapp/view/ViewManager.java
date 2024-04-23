package me.idbi.chatapp.view;

import lombok.Getter;
import me.idbi.chatapp.Main;

@Getter
public class ViewManager {

    private IView currentView;

    public ViewManager() {
        this.currentView = null;
    }

    public void changeView(ViewType view) {
        this.changeView(view.getView());
    }

    public void changeView(IView view) {
        this.currentView = view;
        if(view.isCursor())
            Main.getTerminalManager().showCursor();
        else
            Main.getTerminalManager().hideCursor();
        view.show();
    }

    public void exitView() {
        this.currentView = null;
        Main.getTerminalManager().showCursor();
    }

    public void refresh() {
        if(this.currentView == null) return;
        changeView(this.currentView);
    }
}
