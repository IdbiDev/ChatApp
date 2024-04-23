package me.idbi.chatapp;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.eventmanagers.EventManager;
import me.idbi.chatapp.eventmanagers.interfaces.Listener;
import me.idbi.chatapp.networking.Client;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.networking.Server;
import me.idbi.chatapp.table.TableManager;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.ViewManager;
import me.idbi.chatapp.view.ViewType;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    Todo: Kérdések mr sósmogyorónak or patrik :3:

     Where do putni da IP connect address port cuki
     Where reading postgras pass and user
 */

public class Main implements Listener {

    @Getter private static ViewManager viewManager;
    @Getter private static EventManager eventManager;
    @Getter private static TableManager tableManager;
    @Getter private static Client client;
    @Getter private static TerminalManager terminalManager;
    @Getter @Setter private static Map<String, Room> rooms;


    // Server //
    public static void main(String[] args) throws IOException {
//        Terminal terminal = TerminalBuilder.terminal();
//        terminal.enterRawMode();
//        NonBlockingReader nonBlockingReader = terminal.reader();
//        while (true) {
//            System.out.print(nonBlockingReader.read());
//        }

        eventManager = new EventManager();
        if(args.length == 0) {
            rooms = new HashMap<>();
            terminalManager = new TerminalManager();
            tableManager = new TableManager();
            //Client
            client = new Client("127.0.0.1",5000);
            viewManager = new ViewManager();
            viewManager.changeView(ViewType.LOGIN);
        }else {

            Server server = new Server(5000);
        }

//        viewManager = new ViewManager();
//        tableManager = new TableManager();
//        client = new Client("127.0.0.1",5000);
//
//
//        viewManager.changeView(ViewType.LOGIN);


//        while(true){
//        }
//        while (true){
//            if(manager.isCanWrite()) System.out.print(TerminalManager.Screen.CLEAR_LINE);
//            System.out.print("\r> "+manager.getKeyboardListener().getBuffer());
//        }

    }


    }

