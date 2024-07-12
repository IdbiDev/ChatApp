package me.idbi.chatapp.events.servers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.eventmanagers.interfaces.Event;
import me.idbi.chatapp.networking.Room;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

                System.out.println("Connecting to database.adadasaasadaasaasaasadaadaaddaaasasasadadadadadsssssddddsdssasdssdssddsddsddssddsdss..");
                while (result.next()) {
                    System.out.println("rztzztztztztztzt");
                    Room room = new Room(
                            UUID.fromString(result.getString("uuid")),
                            result.getString("name"),
                            UUID.fromString(result.getString("owner")),
                            result.getString("password"),
                            new CopyOnWriteArrayList<>(),
                            result.getInt("maxmembers"),
                            new CopyOnWriteArrayList<>(),
                            new CopyOnWriteArrayList(Arrays.stream(((String[]) result.getArray("administrators").getArray())).map(UUID::fromString).toList())
                    );

                    Main.getServer().getRooms().put(room.getUniqueId(), room);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        return true;
    }
}
