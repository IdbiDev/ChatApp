package me.idbi.chatapp.database.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseSetQuery implements IDatabaseQuery {

    private final Map<String, Object> queryFilter;

    public DatabaseSetQuery(String key, Object value) {
        this.queryFilter = new HashMap<>();
        this.queryFilter.put(key, value);
    }

    @Override
    public Map<String, Object> getContent() {
        return this.queryFilter;
    }

    @Override
    public IDatabaseQuery append(String key, Object value) {
        this.queryFilter.put(key, value);
        return this;
    }

    @Override
    public IDatabaseQuery append(IDatabaseQuery query) {
        this.queryFilter.putAll(query.getContent());
        return this;
    }

    @Override
    public String getSeparator() {
        return " AND ";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String toString() {
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<String, Object> entry : this.queryFilter.entrySet()) {
            pairs.add(entry.getKey() + " = '" + entry.getValue() + "'");
        }

        return String.join(this.getSeparator(), pairs);
    }
}
