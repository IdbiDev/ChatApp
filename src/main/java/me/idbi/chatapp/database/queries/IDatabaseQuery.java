package me.idbi.chatapp.database.queries;

import java.util.Map;

public interface IDatabaseQuery {

    Map<String, Object> getContent();
    IDatabaseQuery append(String key, Object value);
    IDatabaseQuery append(IDatabaseQuery query);
    String getSeparator();
    int getPriority();
    String toString();
}
