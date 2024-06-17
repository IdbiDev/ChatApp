package me.idbi.chatapp;

import lombok.Getter;
import me.idbi.chatapp.eventmanagers.EventManager;
import me.idbi.chatapp.eventmanagers.interfaces.Listener;
import me.idbi.chatapp.networking.Client;
import me.idbi.chatapp.networking.Server;
import me.idbi.chatapp.packets.client.DebugMessagePacket;
import me.idbi.chatapp.view.ViewType;
import org.apache.commons.cli.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

/*
    Todo: Kérdések mr sósmogyorónak or patrik :3:

     Where do putni da IP connect address port cuki
     Where reading postgras pass and user
 */

public class Main implements Listener {

    @Getter private static int messagePerScroll;
    @Getter private static SimpleDateFormat messageDateFormat;

    @Getter private static ClientData clientData;
    @Getter private static EventManager eventManager;
    @Getter private static Client client;
    @Getter private static int scrollState = 0;

    public static void debug(String message) {
        Main.getClient().sendPacket(new DebugMessagePacket(message));
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
    public static void main(String[] args) throws IOException, InterruptedException {

//        Terminal terminal = TerminalBuilder.terminal();
//        terminal.enterRawMode();
//        NonBlockingReader nonBlockingReader = terminal.reader();
//        boolean c = true;
//        while (c) {
//            System.out.println(nonBlockingReader.read());
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
        messageDateFormat = new SimpleDateFormat("MM.dd HH:mm:ss");
        if (args.length >= 1) {
            if (cmd.hasOption("c")) {
                clientData = new ClientData();
                clientData.load();
                messagePerScroll = 3;
                //Thread.sleep(2000);
                //Client
                client = new Client(host, port);

                clientData.getViewManager().setView(ViewType.LOGIN);
            } else if (cmd.hasOption("s")) {
                Server server = new Server(port);

            }
        }
    }
}

