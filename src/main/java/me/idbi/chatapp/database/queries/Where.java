package me.idbi.chatapp.database.queries;

public class Where {

    public static IDatabaseQuery of(String key, Object value) {
        return new DatabaseWhereQuery(key, value);
    }
}
