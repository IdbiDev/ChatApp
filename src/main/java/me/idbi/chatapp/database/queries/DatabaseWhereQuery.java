package me.idbi.chatapp.database.queries;

import me.idbi.chatapp.database.DatabaseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseWhereQuery implements IDatabaseQuery {

    private final Map<String, Object> queryFilter;

    public DatabaseWhereQuery(String key, Object value) {
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
        return ", ";
    }

    @Override
    public int getPriority() {
        return 1;
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
