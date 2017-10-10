package com.benayn.constell.service.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class Pair<K, V> {

    private K key;
    private V value;

}
