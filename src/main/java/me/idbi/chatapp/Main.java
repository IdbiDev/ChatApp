package me.idbi.chatapp;

import lombok.Getter;
import me.idbi.chatapp.commands.CommandManager;
import me.idbi.chatapp.commands.chatcommands.LeaveCommand;
import me.idbi.chatapp.commands.chatcommands.roommanagers.*;
import me.idbi.chatapp.database.DatabaseManager;
import me.idbi.chatapp.eventmanagers.EventManager;
import me.idbi.chatapp.eventmanagers.interfaces.Listener;
import me.idbi.chatapp.networking.Client;
import me.idbi.chatapp.networking.Member;
import me.idbi.chatapp.networking.Server;
import me.idbi.chatapp.notifications.Notification;
import me.idbi.chatapp.packets.client.DebugMessagePacket;
import me.idbi.chatapp.packets.server.SendNotificationPacket;
import me.idbi.chatapp.view.ViewType;
import org.apache.commons.cli.*;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;



public class Main implements Listener {

    @Getter private static int messagePerScroll;
    @Getter private static SimpleDateFormat messageDateFormat;

    @Getter private static ClientData clientData;
    @Getter private static CommandManager commandManager;
    @Getter private static EventManager eventManager;
    @Getter private static Client client;
    @Getter private static int scrollState = 0;
    @Getter private static Server server;
    @Getter private static DatabaseManager databaseManager;


    public static void debug(String message) {
        try {
            Main.getClient().sendPacket(new DebugMessagePacket(message));
        } catch(Exception ex){

        }

    }

    public static void debugFile(String message) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("debug.txt", true));
            bufferedWriter.write((message + "\r\n"));
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Server //
    public static void startApp(String[] args) throws IOException, InterruptedException, AWTException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, URISyntaxException {
//        Terminal terminal = TerminalBuilder.terminal();
//        terminal.enterRawMode();
//        NonBlockingReader nonBlockingReader = terminal.reader();
//        boolean c = true;
//        while (c) {
//            System.out.println(nonBlockingReader.read());y
//        }

        Options options = new Options();
        Option serverOption = new Option("s", "server", false, "Run as server");
        serverOption.setRequired(false);
        options.addOption(serverOption);

        Option clientOption = new Option("c", "client", false, "Run as client");
        clientOption.setRequired(false);
        options.addOption(clientOption);

        Option hostOption = new Option("h", "host", true, "Server address");
        hostOption.setRequired(false);
        options.addOption(hostOption);

        Option portOption = new Option("p", "port", true, "Server port");
        portOption.setRequired(false);
        options.addOption(portOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        System.setProperty("jna.encoding", "UTF8");

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Usage:", options);
            System.exit(1);
        }

        String host = cmd.getOptionValue("h", "localhost");

        int port;
        if (cmd.hasOption("p")) {
            if (!cmd.getOptionValue("p").matches("^[0-9]+$")) {
                System.out.println("A port nem jó xd");
                System.exit(1);
                return;
            }
            port = Integer.parseInt(cmd.getOptionValue("p"));
        } else {
            port = 5000; // env fileból beolvasni.
        }


        eventManager = new EventManager();
        commandManager = new CommandManager();
        messageDateFormat = new SimpleDateFormat("MM.dd HH:mm:ss");
        if (args.length >= 1) {
            if (cmd.hasOption("c")) {
                Notification.load();
                clientData = new ClientData();
                clientData.load();
                messagePerScroll = 3;
                //Thread.sleep(2000);
                //Client
                client = new Client(host, port);

                clientData.getViewManager().setView(ViewType.LOGIN);
            } else if (cmd.hasOption("s")) {
                databaseManager = new DatabaseManager();
                databaseManager.connect();
                commandManager.registerCommand("leave", "/leave", "Kilépés a jelenlegi szobából", new LeaveCommand());
                commandManager.registerCommand("rename", "/rename <új név>", "Szoba átnevezése", new RoomRenameCommand());
                commandManager.registerCommand("setpassword", "/setpassword <új jelszó>", "Új jelszó beállítása", new RoomSetpasswordCommand(), "setpsw", "setpass");
                commandManager.registerCommand("permanent", "/permanent", "A szoba örökké vagy ideiglenessé tétele", new RoomPermanentCommand());
                commandManager.registerCommand("setowner", "/setowner <új tulajdonos neve>", "Szoba tulajdonjog átruházása", new RoomSetownerCommand());
                commandManager.registerCommand("disband", "/disband", "Szoba azonnali törlése", new RoomDisbandCommand());
                server = new Server(port);
                server.serverLoop();
            }
        }
    }
}

