package me.alek.model;

import java.util.*;

public class DuplicatedValueMap<K, V> {

    private final Map<K, List<V>> map = new HashMap<>();

    public void put(K k, V v) {
        if (map.containsKey(k)) {
            map.get(k).add(v);
        } else {
            List<V> array = new ArrayList<>();
            array.add(v);
            map.put(k, array);
        }
    }

    public List<V> get(K k) {
        return map.get(k);
    }

    public V get(K k, int i) {
        if (map.get(k).size()-1 < i) {
            return null;
        } else {
            return map.get(k).get(i);
        }
    }

    public Set<Map.Entry<K, List<V>>> entrySet() {
        return map.entrySet();
    }

    public double size() {
        double size = 0;
        for (Map.Entry<K, List<V>> entry : map.entrySet()) {
            size += entry.getValue().size();
        }
        return size;
    }

    public List<Map.Entry<K, V>> getPulledEntries() {
        List<Map.Entry<K, V>> pulledEntrySet = new ArrayList<>();
        map.forEach((key, value) -> {
            for (V innerEntryValue : value) {
                pulledEntrySet.add(new AbstractMap.SimpleEntry<>(key, innerEntryValue));
            }
        });
        return pulledEntrySet;
    }
}
