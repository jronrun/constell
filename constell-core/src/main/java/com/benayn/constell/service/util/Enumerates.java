package com.benayn.constell.service.util;

import java.util.EnumSet;
import java.util.function.Predicate;

public final class Enumerates {

    private Enumerates() {}

    /**
     * Returns a enum constant for the given type and predicate
     * @param enumClass
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T find(Class<T> enumClass, Predicate<? super T> predicate) {
        return EnumSet.allOf(enumClass)
            .stream()
            .filter(predicate)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("Unsupported %s", enumClass.getSimpleName())));
    }

}
