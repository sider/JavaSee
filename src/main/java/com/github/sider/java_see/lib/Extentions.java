package com.github.sider.java_see.lib;

import com.github.sider.java_see.Pair;
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
    public static List<?> single(Object object) {
        if(object == null) {
            return List.of();
        } else if(object instanceof List<?>) {
            return (List<?>)object;
        } else {
            return List.of(object);
        }
    }

    public static String repeat(String string, int indent) {
        var builder = new StringBuilder();
        for(int i = 0; i < indent; i++) {
            builder.append(string);
        }
        return new String(builder);
    }
}
