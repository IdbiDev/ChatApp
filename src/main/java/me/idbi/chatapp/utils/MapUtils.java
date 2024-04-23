package me.idbi.chatapp.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    public static Map<Integer, String> insert(Map<Integer, String> map, int key, String value) {
        Map<Integer, String> newMap = new HashMap<Integer, String>();
        for (Map.Entry<Integer, String> hash : map.entrySet()) {
            if(hash.getKey() < key) {
                newMap.put(hash.getKey(), hash.getValue());
                continue;
            } else if(hash.getKey() == key) {
                newMap.put(hash.getKey(), value);
            }
            newMap.put(hash.getKey() + 1, hash.getValue());
        }
        return newMap;
    }
}
