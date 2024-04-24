package me.idbi.chatapp.networking;

import lombok.Getter;
import me.idbi.chatapp.events.clients.ClientLoginEvent;
import me.idbi.chatapp.events.clients.ClientRefreshEvent;
import me.idbi.chatapp.events.clients.ClientRoomJoinEvent;
import me.idbi.chatapp.packets.client.HandshakePacket;
import me.idbi.chatapp.packets.ClientPacket;
import me.idbi.chatapp.packets.client.PongPacket;
import me.idbi.chatapp.packets.server.LoginPacket;
import me.idbi.chatapp.packets.server.PingPacket;
import me.idbi.chatapp.packets.server.ReceiveRefreshPacket;
import me.idbi.chatapp.packets.server.RoomJoinResultPacket;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class Client {

    @Getter private Socket socket;
    private ClientListener listener;
    @Getter
    private boolean canRun = true;
    @Getter String host;
    @Getter int port;


    public Client(String host,int port) {
        this.host = host;
        this.port = port;
    }
    public boolean connect() {
        try {
            this.socket = new Socket(host, port); //server
            this.listener = new ClientListener(this);
            Thread t = new Thread(this.listener);
            t.start();
            ClientPacket packet = new HandshakePacket(System.getenv("USERNAME"));
            sendPacket(packet);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public void sendPacket(ClientPacket packet) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }


    public static class ClientListener implements Runnable {
        private final Client client;
        private final Socket socket;
        ClientListener(Client client) {
            this.client = client;
            this.socket = client.socket;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage()+"?");
                }
                if (!socket.isConnected() || socket.isClosed()) {
                    System.out.println("Socket is closed");
                    break;
                }
                try {
                    if (socket.getInputStream().available() > 0) {
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        Object packetObject = in.readObject();

                        if (packetObject instanceof LoginPacket packet) {
                            new ClientLoginEvent().callEvent();
                        } else if (packetObject instanceof ReceiveRefreshPacket packet) {
                            new ClientRefreshEvent(packet.getRooms()).callEvent();
                        } else if (packetObject instanceof RoomJoinResultPacket packet) {
                            ClientRoomJoinEvent joinEvent = new ClientRoomJoinEvent(packet.getRoom(), packet.getResult());
                            joinEvent.callEvent();
                        } else if (packetObject instanceof PingPacket packet) {
                            client.sendPacket(new PongPacket());
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
