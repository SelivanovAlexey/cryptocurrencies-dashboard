package com.onedigit.utah.util;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CumulativeHashMap<K, V> extends HashMap<K, V> {

    private final Map<K, V> valuesToUpdate = new ConcurrentHashMap<>();

    @Override
    public V put(K key, V value) {
        V result;
        if (this.get(key) != null && this.get(key).equals(value))
            result = super.put(key, value);
        else {
            result = super.put(key, value);
            valuesToUpdate.put(key, value);
        }
        return result;
    }

    public Map<K, V>  getValuesToUpdate() {
        Map<K, V> resultMap = new HashMap<>();
        valuesToUpdate.forEach((k, v) -> resultMap.put(k, valuesToUpdate.remove(k)));
        return resultMap;
    }

    public Map<K,V> getExchangeData(){
        return this;
    }


}
