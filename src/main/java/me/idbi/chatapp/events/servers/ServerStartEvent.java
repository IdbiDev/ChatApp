package me.idbi.chatapp.events.servers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Room;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@AllArgsConstructor
public class ServerStartEvent extends Event {
    private int port;

    @Override
    public boolean callEvent() {
        System.out.println("Server started");

        Main.getDatabaseManager().getDriver().poll("SELECT * FROM rooms").thenAcceptAsync(result -> {
            try {
                int rowCount = 0;
                System.out.println("Loading rooms..");
                while (result.next()) {
                    Room room = new Room(
                            UUID.fromString(result.getString("uuid")),
                            result.getString("name"),
                            Objects.equals(result.getString("owner"), "") ? null : UUID.fromString(result.getString("owner")),
                            Objects.equals(result.getString("password"), "") ? null : result.getString("password"),
                            new CopyOnWriteArrayList<>(),
                            result.getInt("maxmembers"),
                            new CopyOnWriteArrayList<>(),
                            new CopyOnWriteArrayList(Arrays.stream(((String[]) result.getArray("administrators").getArray())).map(UUID::fromString).toList())
                    );
                    Main.getServer().getRooms().put(room.getUniqueId(), room);
                    rowCount++;
                }
                if(rowCount == 0) {
                    Room room = new Room(
                            UUID.randomUUID(),
                            "Beszélgető",
                            null,
                            null,
                            new CopyOnWriteArrayList<>(),
                            0,
                            new CopyOnWriteArrayList<>(),
                            new CopyOnWriteArrayList<>()
                    );
                    Main.getServer().getRooms().put(room.getUniqueId(), room);
                    Main.getDatabaseManager().getDriver().exec(
                            "INSERT INTO rooms (uuid,name,owner,password,maxmembers,administrators) VALUES (?,?,?,?,?,?)",
                            room.getUniqueId(),
                            room.getName(),
                            room.getOwner() != null ? room.getOwner().toString() : "",
                            room.getPassword() != null ? room.getPassword().toString() : "",
                            room.getMaxMembers(),
                            new String[]{}
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Done rooms");
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        return true;
    }
}
