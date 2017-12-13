package com.benayn.constell.service.server.dialect.expression;

import com.benayn.constell.service.util.LZString;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import java.util.Locale;

public class StarExpression {

    public StarExpression(Locale locale) {

    }

    public String encodes(Object target) {
        return LZString.encodes(target);
    }

    public static <K, V> ImmutableMap<K, V> asMap(K k1, V v1) {
        return ImmutableBiMap.of(k1, v1);
    }

    public static <K, V> ImmutableMap<K, V> asMap(K k1, V v1, K k2, V v2) {
        return ImmutableBiMap.of(k1, v1, k2, v2);
    }

    public static <K, V> ImmutableMap<K, V> asMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        return ImmutableBiMap.of(k1, v1, k2, v2);
    }

    public static <K, V> ImmutableMap<K, V> asMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return ImmutableBiMap.of(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    public static <K, V> ImmutableMap<K, V> asMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return ImmutableBiMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }
}
