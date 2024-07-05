package me.idbi.chatapp.database;

import lombok.Getter;
import me.idbi.chatapp.database.queries.DatabaseWhereQuery;
import me.idbi.chatapp.database.queries.Where;

import java.sql.Connection;
import java.sql.DriverManager;

@Getter
public class DatabaseManager {

    private final String host;
    private final int port;

    /**
     * Need to starts with ?
     */
    private final String parameters;

    private final String database;
    private final String username;
    private final String password;

    private Connection connection;

    public DatabaseManager() {
        this.host = "localhost";
        this.port = 3306;
        this.database = "chatapp";
        this.username = "postgres";
        this.password = "admin";
        this.parameters = "";
    }

    public void connect() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.database + parameters, this.username, this.password
            );
            if (this.connection.isValid(0)) {
                System.out.println("Database connected");
            } else {
                System.out.println("Database failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        String query = "UPDATE column SET uuid = ? WHERE " + new DatabaseWhereQuery("UUID", 123).append("NAME", "kbalu");
    }
}
