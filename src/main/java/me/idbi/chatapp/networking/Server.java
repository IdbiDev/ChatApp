package me.idbi.chatapp.networking;

import lombok.Getter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.events.servers.*;
import me.idbi.chatapp.messages.ClientMessage;
import me.idbi.chatapp.messages.IMessage;
import me.idbi.chatapp.messages.SystemMessage;
import me.idbi.chatapp.notifications.Notification;
import me.idbi.chatapp.notifications.Notifications;
import me.idbi.chatapp.packets.ServerPacket;
import me.idbi.chatapp.packets.client.*;
import me.idbi.chatapp.packets.server.*;
import me.idbi.chatapp.utils.RoomJoinResult;
import me.idbi.chatapp.utils.TerminalManager;

import java.io.*;
import java.lang.reflect.AnnotatedType;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
        this.rooms = new ConcurrentHashMap<>();
        this.sockets = new ConcurrentHashMap<>();
        this.heartbeatTable = new ConcurrentHashMap<>();
        this.clientInputStreams = new ConcurrentHashMap<>();
        this.clientOutputStreams = new ConcurrentHashMap<>();
        try {
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
            //this.serverLoop();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendNotification(Member sender, Notification notification) {
        sendPacket(sender, new SendNotificationPacket(notification));
    }

    public void sendNotification(Member sender, Notifications notifications) {
        sendPacket(sender, new SendNotificationPacket(notifications.getNotification()));
    }

    public void sendNotification(Member sender, Notifications notifications, String... args) {
        Notification not = notifications.getNotification();
        not.setDescription(not.getDescription().formatted(args));
        not.setTitle(not.getTitle().formatted(args));
        sendPacket(sender, new SendNotificationPacket(not));
    }

    public void sendNotification(Member sender, Notification notification, String... args) {
        notification.setDescription(notification.getDescription().formatted(args));
        notification.setTitle(notification.getTitle().formatted(args));
        sendPacket(sender, new SendNotificationPacket(notification));
    }

    public void sendPacket(Member sender, ServerPacket packet) {
        if (getSocketByMember(sender) != null)
            sendPacket(getSocketByMember(sender), packet);
        else
            System.out.println("Member nem létezik?");
    }

    private void sendPacket(Socket sender, ServerPacket packet) {
        try {
            ObjectOutputStream out = clientOutputStreams.get(sender);
            out.writeObject(packet);
            out.flush();
            out.reset();
        } catch (SocketException e) {
            new ServerClientDisconnectEvent(sockets.get(sender), ServerClientDisconnectEvent.DisconnectReason.DISCONNECT).callEvent();

            System.out.println(sockets.get(sender).getName() + " disconnected (Socket disconnected)");

            heartbeatTable.remove(sender);
            for (Room room : rooms.values()) {
                if(room.getMembers().contains(sockets.get(sender))) {
                    shouldRefresh = true;
                }
                room.removeMember(sockets.get(sender));
            }
            sockets.remove(sender);
        } catch (IOException e) {
            if (!(packet instanceof PingPacket)) {
                e.printStackTrace();
                return;
            }
            Socket socketMember;
            for (Room room : sockets.get(sender).getRooms()) {
                for (Member m : room.getMembers()) {
                    if ((socketMember = getSocketByMember(m)) == null || socketMember == sender) {
                        continue;
                    }
                    sendPacket(socketMember, new SendMessageToClientPacket(
                            new SystemMessage(
                                    room,
                                    SystemMessage.MessageType.QUIT.setMember(sockets.get(sender)),
                                    1
                            ))
                    );
                }
            }
            new ServerClientDisconnectEvent(sockets.get(sender), ServerClientDisconnectEvent.DisconnectReason.DISCONNECT).callEvent();

            System.out.println(sockets.get(sender).getName() + " disconnected (No connection)");

            heartbeatTable.remove(sender);
            boolean shouldRefresh = false;
            for (Room room : rooms.values()) {
                if (room.getMembers().contains(sockets.get(sender))) {
                    shouldRefresh = true;
                }
                room.removeMember(sockets.get(sender));
            }
            if (shouldRefresh) {
                refreshForKukacEveryoneUwU();
            }
            sockets.remove(sender);
            e.printStackTrace();
            return;
        }
    }

    private Socket getSocketByMember(Member member) {
        if (!this.sockets.containsValue(member)) {
            return null;
        }

        return this.sockets.entrySet().stream().filter(element -> element.getValue().equals(member)).findAny().get().getKey();
    }

    public void serverLoop() {
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
                        for (Member m : room.getMembers()) {
                            if ((socketMember = getSocketByMember(m)) == null || socketMember == socket) {
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
                        if (!clientInputStreams.containsKey(socket)) {
                            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                            clientInputStreams.put(socket, in);
                        }
                        Object packetObject = clientInputStreams.get(socket).readObject();

                        if (packetObject instanceof HandshakePacket packet) {
                            Main.getDatabaseManager().getDriver().poll("SELECT * FROM users WHERE name = ?", packet.getId()).thenAcceptAsync(res -> {
                                try {
                                    if (res.next()) {
                                        sockets.put(socket, new Member(UUID.fromString(res.getString("uuid")), res.getString("name"), res.getString("displayname"), new ArrayList<>(), new HashMap<>()));
                                    } else {
                                        sockets.put(socket, new Member(UUID.randomUUID(), packet.getId(), packet.getId(), new ArrayList<>(), new HashMap<>()));
                                        Main.getDatabaseManager().getDriver().exec("INSERT INTO users (uuid,name,displayname) VALUES (?,?,?)", sockets.get(socket).getUniqueId().toString(), packet.getId(), packet.getId());
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Client login: " + packet.getId());
                                sendPacket(socket, new LoginPacket(sockets.get(socket)));
                            });
                        } else if (packetObject instanceof RequestRefreshPacket) {
                            sendPacket(socket, new ReceiveRefreshPacket(this.rooms));

                        } else if (packetObject instanceof RoomJoinPacket packet) {
                            Room selectedRoom = this.rooms.get(packet.getUniqueId());

                            RoomJoinResult result = RoomJoinResult.SUCCESS;

                            if (selectedRoom == null) {
                                result = RoomJoinResult.INVALID_ROOM;
                            }

                            if (selectedRoom != null && selectedRoom.hasPassword() && !Objects.equals(selectedRoom.getPassword(), packet.getPassword())) {
                                result = RoomJoinResult.WRONG_PASSWORD;
                                //sendPacket(socket, new RoomJoinResultPacket(RoomJoinResult.WRONG_PASSWORD, selectedRoom));
                            }

                            if (selectedRoom != null && selectedRoom.isFull()) {
                                result = RoomJoinResult.ROOM_FULL;
                                //sendPacket(socket, new RoomJoinResultPacket(RoomJoinResult.ROOM_FULL, selectedRoom));
                            }

                            ServerRoomJoinEvent event = new ServerRoomJoinEvent(sockets.get(socket), selectedRoom, result);
                            if (event.callEvent()) {

                                if (result == RoomJoinResult.SUCCESS) {
                                    selectedRoom.addMember(entry.getValue());
                                    Socket socketMember;
                                    SystemMessage msg = new SystemMessage(
                                            selectedRoom,
                                            SystemMessage.MessageType.JOIN.setMember(entry.getValue()),
                                            new Date(),
                                            1
                                    );
                                    selectedRoom.getMessages().add(msg);
                                    for (Member member : selectedRoom.getMembers()) {
                                        if ((socketMember = getSocketByMember(member)) == null) {
                                            continue;
                                        }
                                        sendPacket(socketMember, new SendMessageToClientPacket(msg));


                                        //Send member joined packet

                                    }
                                    refreshForKukacEveryoneUwU();
                                }
                                sendPacket(socket, new RoomJoinResultPacket(event.getResult(), selectedRoom, new Date()));
                            } else {
                                sendPacket(socket, new RoomJoinResultPacket(RoomJoinResult.CANCELLED, selectedRoom, new Date()));
                            }
                        } else if (packetObject instanceof PongPacket) {
                            this.heartbeatTable.get(entry.getKey()).setFailCount(0);
                            this.heartbeatTable.get(entry.getKey()).setLastPing(System.currentTimeMillis());
                        } else if (packetObject instanceof DebugMessagePacket a) {
                            System.out.println(a.getMessage());
                        } else if (packetObject instanceof SendMessageToServerPacket packet) {
                            Room selectedRoom = this.rooms.get(packet.getMessage().getRoom().getUniqueId());
                            ServerReceiveMessageEvent event = new ServerReceiveMessageEvent(packet.getMessage());
                            IMessage msg = event.getMessage();
                            msg.setDate(new Date());

                            String[] args = event.getMessage().getRawMessage().split(" ");
                            if (event.getMessage().getRawMessage().startsWith("/")) {
                                ServerCommandPreprocessEvent cmdEvent = new ServerCommandPreprocessEvent(
                                        entry.getValue(), selectedRoom, args[0], Arrays.copyOfRange(args, 1, args.length));

                                cmdEvent.callEvent();
                                Main.getCommandManager().callCommand(
                                        cmdEvent.getMember(),
                                        cmdEvent.getRoom(),
                                        cmdEvent.getCommand().replaceFirst("/", ""),
                                        cmdEvent.getArgs()
                                );
                                continue;
                            }
                            if (event.callEvent()) {
                                Socket socketMember;

                                IMessage tempMsg = event.getMessage();
                                StringBuilder builder = new StringBuilder();
                                outer:
                                for (String s : tempMsg.getRawMessage().split(" ")) {
                                    if (!s.startsWith("@")) {
                                        builder.append(s).append(" ");
                                        continue;
                                    }

                                    String name = s.replace("@", "");
                                    for (Member member : selectedRoom.getMembers()) {
                                        if (!member.getName().equalsIgnoreCase(name)) continue;
                                        builder.append(TerminalManager.Color.YELLOW).append("@").append(member.getName()).append(TerminalManager.Color.RESET).append(" ");
                                        sendNotification(member, Notifications.MENTION, entry.getValue().getDisplayName());
                                        continue outer;
                                    }
                                    builder.append(s).append(" ");
                                }

                                event.getMessage().setMessage(builder.toString().strip());
                                selectedRoom.getMessages().add(event.getMessage());

                                for (Member member : selectedRoom.getMembers()) {
                                    if ((socketMember = getSocketByMember(member)) == null) {
                                        continue;
                                    }
                                    sendPacket(socketMember, new SendMessageToClientPacket(event.getMessage()));
                                }
                            }
                        } else if (packetObject instanceof CreateRoomPacket packet) {
                            Room newRoom = new Room(UUID.randomUUID(), packet.getName(), entry.getValue().getUniqueId(), packet.getPassword(), new ArrayList<>(), packet.getMaxMembers(), new ArrayList<>(), new ArrayList<>());
                            ServerRoomCreateEvent event = new ServerRoomCreateEvent(newRoom);
                            if (event.callEvent()) {
                                newRoom = event.getRoom();

                                this.rooms.put(newRoom.getUniqueId(), newRoom);
                                entry.getValue().getRooms().add(newRoom);

                                newRoom.addMember(entry.getValue());


                                SystemMessage msg = new SystemMessage(
                                        newRoom,
                                        SystemMessage.MessageType.ROOM_CREATE.setRoom(newRoom.getName()),
                                        new Date(),
                                        1);
                                newRoom.getMessages().add(msg);

                                sendPacket(socket, new RoomJoinResultPacket(RoomJoinResult.SUCCESS, newRoom, new Date()));

                                refreshForKukacEveryoneUwU();
                                Main.getDatabaseManager().getDriver().exec(
                                        "INSERT INTO rooms (uuid,name,owner,password,maxmembers,administrators) VALUES (?,?,?,?,?,?)",
                                        newRoom.getUniqueId().toString(),
                                        newRoom.getName(),
                                        newRoom.getOwner() != null ? newRoom.getOwner().toString() : "",
                                        newRoom.getPassword() != null ? newRoom.getPassword() : "",
                                        newRoom.getMaxMembers(),
                                        new String[]{}
                                );

                            }
                        }
                    }
                } catch (IOException | ClassNotFoundException | ClassCastException | IllegalStateException e) {
                    System.out.println("Error while parsing client!");
                    try {
                        sendPacket(socket, new ShutdownPacket());
                        clientInputStreams.get(socket).close();

                        sockets.remove(socket);
                        heartbeatTable.remove(socket);
                        for (Room room : rooms.values()) {
                            room.removeMember(sockets.get(socket));
                        }
                        socket.close();
                    } catch (IOException ex) {
                        System.out.println("Error while handling shutdown packet!");
                    }
                    e.printStackTrace();
                }
            }
        }
    }

    public void refreshForKukacEveryoneUwU() {
        for (Member member : sockets.values()) {
            sendPacket(member, new ReceiveRefreshPacket(this.rooms));
        }
    }


    public static class ConnectionListener implements Runnable {

        private final Server server;

        public ConnectionListener(Server server) {
            this.server = server;
        }

        @Override
        public void run() {
            Socket socket = null;
            while (true) {
                try {
                    socket = this.server.serverSocket.accept();
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    this.server.clientOutputStreams.put(socket, out);
                    this.server.sockets.put(socket, new Member(UUID.randomUUID(), "", "", new ArrayList<>(), new HashMap<>()));
                    System.out.println("Accepted connection from " + socket.getRemoteSocketAddress());
                    this.server.heartbeatTable.put(socket, new PingPongMember(0, 0));
                    socket = null;
                } catch (IOException e) {
                    System.out.println("Accept failed!");
                    if (socket != null) {
                        this.server.clientOutputStreams.remove(socket);
                        this.server.sockets.remove(socket);
                        this.server.heartbeatTable.remove(socket);
                        socket = null;
                    }
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
                    for (Map.Entry<Socket, PingPongMember> entry : this.server.heartbeatTable.entrySet()) {
                        if (entry.getValue().getLastPing() + 5500 > System.currentTimeMillis()) {
                            this.server.sendPacket(entry.getKey(), new PingPacket());
                            continue;
                        }
                        entry.getValue().setFailCount(entry.getValue().getFailCount() + 1);

                        if (entry.getValue().getFailCount() <= 5) {
                            continue;
                        }

                        // disconnect
                        Socket socketMember;
                        for (Room room : this.server.sockets.get(entry.getKey()).getRooms()) {
                            for (Member m : room.getMembers()) {
                                if ((socketMember = this.server.getSocketByMember(m)) == null || socketMember == entry.getKey()) {
                                    continue;
                                }
                                this.server.sendPacket(socketMember, new SendMessageToClientPacket(
                                        new SystemMessage(
                                                room,
                                                SystemMessage.MessageType.QUIT.setMember(this.server.sockets.get(entry.getKey())),
                                                1
                                        ))
                                );
                            }
                        }

                        new ServerClientDisconnectEvent(this.server.sockets.get(entry.getKey()), ServerClientDisconnectEvent.DisconnectReason.DISCONNECT).callEvent();

                        System.out.println(this.server.sockets.get(entry.getKey()).getName() + " disconnected by timeout");

                        this.server.heartbeatTable.remove(entry.getKey());

                        boolean shouldRefresh = false;
                        for (Room room : this.server.getRooms().values()) {
                            if (room.getMembers().contains(this.server.sockets.get(entry.getKey()))) {
                                shouldRefresh = true;
                            }
                            room.removeMember(this.server.sockets.get(entry.getKey()));
                        }

                        if (shouldRefresh) {
                            this.server.refreshForKukacEveryoneUwU();
                        }

                        this.server.sockets.remove(entry.getKey());
                    }
                } catch (InterruptedException e) {
                    System.out.println("Thread stopped");
                }
            }
        }
    }
}
