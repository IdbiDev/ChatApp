package me.idbi.chatapp.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseQuery {

    private final Map<String, String> queryFilter;

    public DatabaseQuery(String key, String value) {
        this.queryFilter = new HashMap<>();
        this.queryFilter.put(key, value);
    }

    public DatabaseQuery append(String key, String value) {
        this.queryFilter.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : this.queryFilter.entrySet()) {
            pairs.add(entry.getKey() + " = '" + entry.getValue() + "'");
        }

        return String.join(", ", pairs);
    }
}

// UPDATE column SET uuid = 123 WHERE uuid = 321 AND name = kbalu

