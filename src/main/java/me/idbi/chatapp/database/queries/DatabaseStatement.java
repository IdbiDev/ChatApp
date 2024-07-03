package me.idbi.chatapp.database.queries;

public class DatabaseStatement {

    public static DatabaseUpdateStatement update(String table, IDatabaseQuery... queries) {
        return new DatabaseUpdateStatement(table, queries);
    }
}
