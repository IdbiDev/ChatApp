package me.idbi.chatapp.networking;

import lombok.Getter;
import lombok.Setter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.events.clients.ClientLoginEvent;
import me.idbi.chatapp.events.clients.ClientMessageEvent;
import me.idbi.chatapp.events.clients.ClientRefreshEvent;
import me.idbi.chatapp.events.clients.ClientRoomJoinEvent;
import me.idbi.chatapp.messages.ClientMessage;
import me.idbi.chatapp.packets.client.HandshakePacket;
import me.idbi.chatapp.packets.ClientPacket;
import me.idbi.chatapp.packets.client.PongPacket;
import me.idbi.chatapp.packets.client.SendMessageToServerPacket;
import me.idbi.chatapp.packets.server.*;
import me.idbi.chatapp.view.ViewType;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class Client {

    @Getter private Socket socket;

    private ClientListener listener;
    @Getter @Setter
    private boolean canRun = true;
    @Getter private String host;
    @Getter private int port;
    @Getter private String name;
    private ObjectInputStream in;
    private static ObjectOutputStream out;


    public Client(String host,int port) {
        this.host = host;
        this.port = port;
        this.name = System.getProperty("user.name");

    }
    public boolean connect() {
        try {
            this.socket = new Socket(host, port); //server
            canRun = true;
            this.listener = new ClientListener(this);
            Thread t = new Thread(this.listener);

            t.start();
            ClientPacket packet = new HandshakePacket(this.name);
            sendPacket(packet);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void sendPacket(ClientPacket packet) {
        try {
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            Main.getClientData().getViewManager().setView(ViewType.SERVER_SHUTDOWN);
        }

    }
    public static class ClientTester implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 80; i++) {
                Main.getClient().sendPacket(new SendMessageToServerPacket(
                        new ClientMessage(Main.getClient().getName(), Main.getClientData().getCurrentRoom(), "Cica csomag Bazsi Peti meleg mint boti buzi HCF plus gyere Lol fokhjgokpopgh " + i)
                ));
            }

        }
    }

    public static class ClientListener implements Runnable {
        private final Client client;
        private final Socket socket;
        ClientListener(Client client)  {
            this.client = client;
            this.socket = client.socket;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            while (client.canRun) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage()+"?");
                }
                if (!socket.isConnected() || socket.isClosed()) {
                    System.out.println("Socket is closed");
                    break;
                }
                try {

                    if (socket.getInputStream().available() > 0) {
                        if(client.in == null) {
                            client.in = new ObjectInputStream(socket.getInputStream());
                        }
                        Object packetObject = client.in.readObject();

                        if (packetObject instanceof LoginPacket packet) {
                            Main.getClientData().setClientMember(packet.getLoginedMember());
                            new ClientLoginEvent(packet.getLoginedMember()).callEvent();
                        } else if (packetObject instanceof ReceiveRefreshPacket packet) {
                            new ClientRefreshEvent(packet.getRooms()).callEvent();
                        } else if (packetObject instanceof RoomJoinResultPacket packet) {
                            ClientRoomJoinEvent joinEvent = new ClientRoomJoinEvent(packet.getRoom(), packet.getResult(), packet.getJoinAt());
                            joinEvent.callEvent();
                        } else if (packetObject instanceof PingPacket packet) {
                            client.sendPacket(new PongPacket());
                        } else if (packetObject instanceof SendMessageToClientPacket packet) {
                            ClientMessageEvent event = new ClientMessageEvent(packet.getMessage());
                            event.callEvent();
                            Main.getClientData().getCurrentRoom().sendMessage(event.getMessage());
                            //System.out.println(event.getMessage().getMessage());
                        } else if(packetObject instanceof ShutdownPacket) {
                            Main.getClientData().getViewManager().setView(ViewType.SERVER_SHUTDOWN);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("ERROR while reading!");
                    e.printStackTrace();
                }
            }
        }
    }
}
