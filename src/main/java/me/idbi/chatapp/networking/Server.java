package me.idbi.chatapp.networking;

import lombok.Getter;
import me.idbi.chatapp.events.servers.ServerClientDisconnectEvent;
import me.idbi.chatapp.events.servers.ServerReceiveMessageEvent;
import me.idbi.chatapp.events.servers.ServerRoomJoinEvent;
import me.idbi.chatapp.events.servers.ServerStartEvent;
import me.idbi.chatapp.messages.SystemMessage;
import me.idbi.chatapp.packets.ServerPacket;
import me.idbi.chatapp.packets.client.*;
import me.idbi.chatapp.packets.server.*;
import me.idbi.chatapp.utils.RoomJoinResult;
import me.idbi.chatapp.utils.TerminalManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Server {

    private ServerSocket serverSocket;
    private ConnectionListener listener;
    private HeartBeatTimer heartbeatListener;
    private final Map<Socket, Member> sockets;
    private final Map<Socket, PingPongMember> heartbeatTable;
    private final Map<Socket, ObjectInputStream> clientInputStreams;
    private final Map<Socket, ObjectOutputStream> clientOutputStreams;


    private final Map<UUID, Room> rooms;

    public Server(int port) {
        this.rooms = new HashMap<>();
        this.sockets = new ConcurrentHashMap<>();
        this.heartbeatTable = new ConcurrentHashMap<>();
        this.clientInputStreams = new ConcurrentHashMap<>();
        this.clientOutputStreams = new ConcurrentHashMap<>();
        try {
            createRoom("GYVAKK admin",null,"admin",10);
            createRoom("Beszélgető",null,null,999);
            createRoom("Patrik szobája",null,"kys",2);
            this.serverSocket = new ServerSocket(port);

            //this.serverSocket.bind(new InetSocketAddress(port));
            this.listener = new ConnectionListener(this);
            Thread t1 = new Thread(this.listener);
            t1.start();
            this.heartbeatListener = new HeartBeatTimer(this);
            Thread t2 = new Thread(this.heartbeatListener);
            t2.start();
            new ServerStartEvent(port).callEvent();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Server shutting down");

                for (Map.Entry<Socket, Member> entry : this.sockets.entrySet()) {
                    sendPacket(entry.getKey(), new ShutdownPacket());
                }
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            this.serverLoop();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendPacket(Socket receiver, ServerPacket packet) {
        try {
            ObjectOutputStream out = clientOutputStreams.get(receiver);
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            if (packet instanceof PingPacket) {
                Socket socketMember;
                for (Room room : sockets.get(receiver).getRooms()) {
                    for(Member m : room.getMembers()) {
                        if((socketMember = getSocketByMember(m)) == null || socketMember == receiver) {
                            continue;
                        }
                        sendPacket(socketMember, new SendMessageToClientPacket(
                                        new SystemMessage(
                                                room,
                                                SystemMessage.MessageType.QUIT.setMember(sockets.get(receiver)),
                                                1
                                        ))
                        );
                    }
                }
                new ServerClientDisconnectEvent(sockets.get(receiver), ServerClientDisconnectEvent.DisconnectReason.DISCONNECT).callEvent();

                System.out.println(sockets.get(receiver).getName() + " disconnected (No connection)");

                heartbeatTable.remove(receiver);
                for (Room room : rooms.values()) {
                    room.removeMember(sockets.get(receiver));
                }
                sockets.remove(receiver);
                return;
            }
            e.printStackTrace();
        }
    }

    private Socket getSocketByMember(Member member) {
        if(!sockets.containsValue(member)) {
            return null;
        }
        return sockets.entrySet().stream().filter(element -> element.getValue().equals(member)).findAny().get().getKey();
    }

    private void serverLoop() {
        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (Map.Entry<Socket, Member> entry : this.sockets.entrySet()) {
                Socket socket = entry.getKey();

                if (!socket.isConnected() || socket.isClosed()) {

                    System.out.println("Socket is closed");
                    Socket socketMember;
                    for (Room room : sockets.get(socket).getRooms()) {
                        for(Member m : room.getMembers()) {
                            if((socketMember = getSocketByMember(m)) == null || socketMember == socket) {
                                continue;
                            }
                            sendPacket(socketMember, new SendMessageToClientPacket(
                                    new SystemMessage(
                                            room,
                                            SystemMessage.MessageType.QUIT.setMember(sockets.get(socket)),
                                            1
                                    ))
                            );
                        }
                    }
                    new ServerClientDisconnectEvent(sockets.get(entry.getKey()), ServerClientDisconnectEvent.DisconnectReason.DISCONNECT).callEvent();
                    for (Room room : rooms.values()) {
                        room.removeMember(entry.getValue());
                    }
                    this.sockets.remove(socket);
                    heartbeatTable.remove(socket);
                }
                try {
                    if (socket.getInputStream().available() > 0) {
                        if(!clientInputStreams.containsKey(socket)) {
                            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                            clientInputStreams.put(socket, in);
                        }
                        Object packetObject = clientInputStreams.get(socket).readObject();

                        if (packetObject instanceof HandshakePacket packet) {
                            System.out.println("Loginolt: " + packet.getId());
                            sockets.put(socket, new Member(packet.getId(), new ArrayList<>(),new HashMap<>()));
                            sendPacket(socket, new LoginPacket(sockets.get(socket)));

                        } else if (packetObject instanceof RequestRefreshPacket) {
                            sendPacket(socket, new ReceiveRefreshPacket(this.rooms));

                        } else if(packetObject instanceof RoomJoinPacket packet) {
                            Room selectedRoom = this.rooms.get(packet.getUniqueId());

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
                                sendPacket(socket, new RoomJoinResultPacket(event.getResult(), selectedRoom, new Date()));
                                if(result == RoomJoinResult.SUCCESS) {
                                    selectedRoom.addMember(entry.getValue());
                                    Socket socketMember;
                                    SystemMessage msg = new SystemMessage(
                                            selectedRoom,
                                            SystemMessage.MessageType.JOIN.setMember(entry.getValue()),
                                            1
                                    );
                                    for(Member member : selectedRoom.getMembers()) {
                                        if((socketMember = getSocketByMember(member)) == null) {
                                            continue;
                                        }
                                        sendPacket(socketMember, new SendMessageToClientPacket(msg));

                                        //Send member joined packet

                                    }
                                    for (Map.Entry<Socket, Member> socketMemberEntry : this.sockets.entrySet()) {
                                        sendPacket(socketMemberEntry.getKey(), new ReceiveRefreshPacket(this.rooms));
                                    }
                                }
                            } else {
                                sendPacket(socket, new RoomJoinResultPacket(RoomJoinResult.CANCELLED, selectedRoom, new Date()));
                            }
                        } else if (packetObject instanceof PongPacket) {
                            this.heartbeatTable.get(entry.getKey()).setFailCount(0);
                            this.heartbeatTable.get(entry.getKey()).setLastPing(System.currentTimeMillis());
                        } else if (packetObject instanceof DebugMessagePacket a) {
                            System.out.println(a.getMessage());
                        }

                        else if (packetObject instanceof SendMessageToServerPacket packet) {
                            Room selectedRoom = this.rooms.get(packet.getMessage().getRoom().getUniqueId());
                            ServerReceiveMessageEvent event = new ServerReceiveMessageEvent(packet.getMessage());
                            packet.getMessage().setDate(new Date());

                            selectedRoom.getMessages().add(packet.getMessage());

                            System.out.println(packet.getMessage().getMessage());
                            if(event.callEvent()) {
                                Socket socketMember;
                                for (Member member : selectedRoom.getMembers()) {
                                    if((socketMember = getSocketByMember(member)) == null) {
                                        continue;
                                    }

                                    sendPacket(socketMember, new SendMessageToClientPacket(event.getMessage()));
                                }

                            }
                        }else if (packetObject instanceof CreateRoomPacket packet) {
                            Room newRoom = new Room(UUID.randomUUID(),packet.getName(),entry.getValue(),packet.getPassword(),new ArrayList<>(),packet.getMaxMembers(),new ArrayList<>());
                            this.rooms.put(newRoom.getUniqueId(), newRoom);

                            newRoom.addMember(entry.getValue());
                            SystemMessage msg = new SystemMessage(newRoom, TerminalManager.Color.GREEN.getCode() + SystemMessage.MessageType.ROOM_CREATE.setRoom(newRoom.getName()) + TerminalManager.Color.RESET, new Date(), 1);
                            newRoom.getMessages().add(msg);
                            sendPacket(socket, new RoomJoinResultPacket(RoomJoinResult.SUCCESS, newRoom, new Date()));
                            for (Map.Entry<Socket, Member> socketMemberEntry : this.sockets.entrySet()) {
                                sendPacket(socketMemberEntry.getKey(), new ReceiveRefreshPacket(this.rooms));
                            }
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("ERROR while reading!");
                    try {
                        sendPacket(socket,new ShutdownPacket());
                        clientInputStreams.get(socket).close();

                        sockets.remove(socket);
                        heartbeatTable.remove(socket);
                        for (Room room : rooms.values()) {
                            room.removeMember(sockets.get(socket));
                        }
                        socket.close();
                    } catch (IOException ex) {
                        System.out.println("GÁZ VAN");
                    }
                    e.printStackTrace();
                }
            }
        }
    }

    private void createRoom(String name, Member owner, String password, int maxMembers) {
        UUID uuid = UUID.randomUUID();
        this.rooms.put(uuid, new Room(uuid, name, owner, password, new ArrayList<>(), maxMembers, new ArrayList<>()));
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
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    server.clientOutputStreams.put(socket,out);
                    server.sockets.put(socket, new Member("",new ArrayList<>(), new HashMap<>()));
                    System.out.println("Accepted connection from222222 " + socket.getRemoteSocketAddress());
                    server.heartbeatTable.put(socket,new PingPongMember(0,0));

                } catch (IOException e) {
                    System.out.println("Accept failed");
                    e.printStackTrace();
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
                    Thread.sleep(1000);
                    for (Map.Entry<Socket, PingPongMember> entry : server.heartbeatTable.entrySet()) {
                        if(entry.getValue().getLastPing() + 5500 <= System.currentTimeMillis()) {
                            entry.getValue().setFailCount(entry.getValue().getFailCount() + 1);
                            if (entry.getValue().getFailCount() > 999999999) {
                                // disconnect
                                Socket socketMember;
                                for (Room room : server.sockets.get(entry.getKey()).getRooms()) {
                                    for(Member m : room.getMembers()) {
                                        if((socketMember = server.getSocketByMember(m)) == null || socketMember == entry.getKey()) {
                                            continue;
                                        }
                                        server.sendPacket(socketMember, new SendMessageToClientPacket(
                                                new SystemMessage(
                                                        room,
                                                        SystemMessage.MessageType.QUIT.setMember(server.sockets.get(entry.getKey())),
                                                        1
                                                ))
                                        );
                                    }
                                }
                                new ServerClientDisconnectEvent(server.sockets.get(entry.getKey()), ServerClientDisconnectEvent.DisconnectReason.DISCONNECT).callEvent();
                                System.out.println(server.sockets.get(entry.getKey()).getName() + " disconnected from timeout");
                                server.heartbeatTable.remove(entry.getKey());
                                for (Room room : server.rooms.values()) {
                                    room.removeMember(server.sockets.get(entry.getKey()));
                                }
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
