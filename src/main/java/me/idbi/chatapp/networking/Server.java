package me.idbi.chatapp.networking;

import lombok.Getter;
import me.idbi.chatapp.events.clients.ClientLoginEvent;
import me.idbi.chatapp.events.servers.ServerRoomJoinEvent;
import me.idbi.chatapp.packets.ServerPacket;
import me.idbi.chatapp.packets.client.HandshakePacket;
import me.idbi.chatapp.packets.client.PongPacket;
import me.idbi.chatapp.packets.client.RequestRefreshPacket;
import me.idbi.chatapp.packets.client.RoomJoinPacket;
import me.idbi.chatapp.packets.server.LoginPacket;
import me.idbi.chatapp.packets.server.PingPacket;
import me.idbi.chatapp.packets.server.ReceiveRefreshPacket;
import me.idbi.chatapp.packets.server.RoomJoinResultPacket;
import me.idbi.chatapp.utils.RoomJoinResult;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Server {

    private ServerSocket serverSocket;
    private ConnectionListener listener;
    private HeartBeatTimer heartbeatListener;
    private final Map<Socket, Member> sockets;
    private final Map<Socket, PingPongMember> heartbeatTable;

    private final Map<String, Room> rooms;

    public Server(int port) {
        this.sockets = new ConcurrentHashMap<>();
        this.rooms = new HashMap<>();
        this.heartbeatTable = new ConcurrentHashMap<>();
        try {
            this.rooms.put("Beszélgető",new Room("Beszélgető",null, null, new ArrayList<>(), 10));
            this.rooms.put("Patrik szobája",new Room("Patrik szobája",null, "szeretemakekszetésaszexet<3", new ArrayList<>(), 2));
            this.rooms.put("GYVAKK Admin",new Room("GYVAKK Admin",null, "admin", new ArrayList<>(), 10));
            this.serverSocket = new ServerSocket(port);
            this.listener = new ConnectionListener(this);
            System.out.println("Started thread");
            Thread t = new Thread(this.listener);
            t.start();
            this.heartbeatListener = new HeartBeatTimer(this);
            t = new Thread(this.heartbeatListener);
            t.start();
            this.serverLoop();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendPacket(Socket receiver, ServerPacket packet) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(receiver.getOutputStream());
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            if (packet instanceof PingPacket) {
                System.out.println(sockets.get(receiver).getName() + " disconnected");
                heartbeatTable.remove(receiver);
                sockets.remove(receiver);
                return;
            }
            e.printStackTrace();
        }

    }

    private void serverLoop() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (Map.Entry<Socket, Member> entry : this.sockets.entrySet()) {
                Socket socket = entry.getKey();

                if (!socket.isConnected() || socket.isClosed()) {

                    System.out.println("Socket is closed");
                    this.sockets.remove(socket);
                    heartbeatTable.remove(socket);
                }
                try {
                    if (socket.getInputStream().available() > 0) {
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        Object packetObject = in.readObject();

                        if (packetObject instanceof HandshakePacket packet) {
                            System.out.println("Loginolt: " + packet.getId());
                            sockets.put(socket, new Member(packet.getId(), new ArrayList<>(),new HashMap<>()));
                            sendPacket(socket, new LoginPacket());

                        } else if (packetObject instanceof RequestRefreshPacket packet) {
                            sendPacket(socket, new ReceiveRefreshPacket(this.rooms));

                        } else if(packetObject instanceof RoomJoinPacket packet) {
                            Room selectedRoom = this.rooms.get(packet.getName());

                            RoomJoinResult result = RoomJoinResult.SUCCESS;

                            if (selectedRoom == null) {
                                result = RoomJoinResult.INVALID_ROOM;
                            }

                            if(selectedRoom != null && !Objects.equals(selectedRoom.getPassword(), packet.getPassword())) {
                                result = RoomJoinResult.WRONG_PASSWORD;
                                //sendPacket(socket, new RoomJoinResultPacket(RoomJoinResult.WRONG_PASSWORD, selectedRoom));
                            }

                            if (selectedRoom != null && selectedRoom.getMembers().size() >= selectedRoom.getMaxMembers()) {
                                result = RoomJoinResult.ROOM_FULL;
                                //sendPacket(socket, new RoomJoinResultPacket(RoomJoinResult.ROOM_FULL, selectedRoom));
                            }

                            ServerRoomJoinEvent event = new ServerRoomJoinEvent(sockets.get(socket), selectedRoom, result);
                            if(event.callEvent()) {
                                sendPacket(socket, new RoomJoinResultPacket(event.getResult(), selectedRoom));
                                if(result == RoomJoinResult.SUCCESS) {
                                    selectedRoom.getMembers().add(entry.getValue());
                                    for(Member member : selectedRoom.getMembers()) {
                                        //Send welcome system message
                                        //Send member joined packet

                                    }
                                }
                            } else {
                                sendPacket(socket, new RoomJoinResultPacket(RoomJoinResult.CANCELLED, selectedRoom));
                            }
                        } else if (packetObject instanceof PongPacket pack) {
                            this.heartbeatTable.get(entry.getKey()).setFailCount(0);
                            this.heartbeatTable.get(entry.getKey()).setLastPing(System.currentTimeMillis());
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("ERROR while reading!");
                    e.printStackTrace();
                }
            }
        }
    }


    public static class ConnectionListener implements Runnable {

        private final Server server;

        public ConnectionListener(Server server) {
            this.server = server;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Socket socket = server.serverSocket.accept();
                    server.sockets.put(socket, new Member("asd",new ArrayList<>(), new HashMap<>()));
                    System.out.println("Accepted connection from222222 " + socket.getRemoteSocketAddress());
                    server.heartbeatTable.put(socket,new PingPongMember());
                } catch (IOException e) {
                    System.out.println("Accept failed");
                }
            }
        }
    }
    public static class HeartBeatTimer implements Runnable {

        private final Server server;

        public HeartBeatTimer(Server server) {
            this.server = server;
        }

        @Override
        public void run() {
            while (true) {

                try {
                    Thread.sleep(5000);
                    for (Map.Entry<Socket, PingPongMember> entry : server.heartbeatTable.entrySet()) {
                        System.out.println(entry.getValue().getLastPing());
                        if(entry.getValue().getLastPing()+5500 <= System.currentTimeMillis()) {
                            entry.getValue().setFailCount(entry.getValue().getFailCount()+1);
                            if (entry.getValue().getFailCount() > 3) {
                                // disconnect
                                System.out.println(server.sockets.get(entry.getKey()).getName() + " disconnected");
                                server.heartbeatTable.remove(entry.getKey());
                                server.sockets.remove(entry.getKey());

                                continue;
                            }
                        }
                        server.sendPacket(entry.getKey(), new PingPacket());
                    }
                } catch (InterruptedException e) {
                    System.out.println("Thread stopped");
                }
            }
        }
    }
}
