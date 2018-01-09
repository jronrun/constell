package com.benayn.constell.service.server.respond;

public interface OptionValue<V> {

    String getLabel();

    V getValue();

    default String stringValue() {
        return String.valueOf(getValue());
    }

}
