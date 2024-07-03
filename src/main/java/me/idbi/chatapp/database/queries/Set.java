package me.idbi.chatapp.database.queries;

public class Set {

    public static DatabaseSetQuery of(String key, Object value) {
        return new DatabaseSetQuery(key, value);
    }
}
