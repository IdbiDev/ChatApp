package me.idbi.chatapp;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.eventmanagers.EventManager;
import me.idbi.chatapp.eventmanagers.interfaces.Listener;
import me.idbi.chatapp.networking.Client;
import me.idbi.chatapp.networking.Room;
import me.idbi.chatapp.networking.Server;
import me.idbi.chatapp.table.TableManager;
import me.idbi.chatapp.utils.InputManager;
import me.idbi.chatapp.utils.TerminalManager;
import me.idbi.chatapp.view.ViewManager;
import me.idbi.chatapp.view.ViewType;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

    @Getter private static int messagePerScroll;
    @Getter @Setter
    private static Room currentRoom;
    @Getter private static SimpleDateFormat messageDateFormat;
    @Getter private static ViewManager viewManager;
    @Getter private static EventManager eventManager;
    @Getter private static InputManager inputManager;
    @Getter private static TableManager tableManager;
    @Getter private static Client client;
    @Getter private static TerminalManager terminalManager;
    @Getter @Setter private static Map<String, Room> rooms;


    // Server //
    public static void main(String[] args) throws IOException, InterruptedException {
//        Terminal terminal = TerminalBuilder.terminal();
//        terminal.enterRawMode();
//        NonBlockingReader nonBlockingReader = terminal.reader();
//        while (true) {
//            System.out.print(nonBlockingReader.read());
//        }

        eventManager = new EventManager();
        messageDateFormat = new SimpleDateFormat("MM.dd HH:mm");
        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("-client")) {
                messagePerScroll = 3;
                inputManager = new InputManager();
                rooms = new HashMap<>();
                terminalManager = new TerminalManager();
                //Thread.sleep(2000);
                tableManager = new TableManager();
                //Client
                client = new Client("192.168.1.130", 5000);
                viewManager = new ViewManager();
                viewManager.changeView(ViewType.LOGIN);
            } else if(args[0].equalsIgnoreCase("-server")) {
                Server server = new Server(5000);
            }
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

