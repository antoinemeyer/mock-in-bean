package com.teketik.test.mockinbean;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Cheap alternative to LazyMap.
 * @author Antoine Meyer
 */
final class MapUtils {

    private MapUtils() {}

    /**
     * @param <K>
     * @param <V>
     * @param map
     * @param key
     * @param valueSupplier
     * @return {@code map.get(key)} if it exists or the value supplied by {@code valueSupplier} (atomically stored in {@code map}).
     */
    public static <K, V> V getOrPut(Map<K, V> map, K key, Supplier<V> valueSupplier) {
        synchronized (map) {
            V value = map.get(key);
            if (value == null) {
                value = valueSupplier.get();
                map.put(key, value);
            }
            return value;
        }
    }

}
