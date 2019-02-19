package com.github.kmizu.jquerly;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class Extentions {
    public static <K, V> List<Pair<K, V>> toList(Map<K, V> map) {
        List<Pair<K, V>> result = Lists.newArrayList();
        for(K key:map.keySet()) {
            result.add(new Pair<>(key, map.get(key)));
        }
        return result;
    }
}
