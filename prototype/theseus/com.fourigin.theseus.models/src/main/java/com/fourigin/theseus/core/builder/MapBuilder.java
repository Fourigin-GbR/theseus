package com.fourigin.theseus.core.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapBuilder<K, V> {
    private Map<K, V> map = new HashMap<>();

    public MapBuilder<K, V> clear(){
        this.map.clear();
        return this;
    }

    public MapBuilder<K, V> put(K key, V value){
        Objects.requireNonNull(key, "key must not be null!");
        Objects.requireNonNull(value, "value must not be null!");

        map.put(key, value);
        return this;
    }

    public Map<K, V> build(){
        return new HashMap<>(map);
    }
}
