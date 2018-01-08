package com.benayn.constell.service.server.respond;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Function;

public class DataExchange {

    private DataExchange() { }

    private Map<String, Function> exchanges = Maps.newHashMap();

    public static DataExchange create() {
        return new DataExchange();
    }

    public static DataExchange of(String filed, Function exchange) {
        return create().add(filed, exchange);
    }

    public DataExchange add(String field, Function exchange) {
        exchanges.put(field, exchange);
        return this;
    }

    public Function get(String field) {
        return exchanges.get(field);
    }

    public boolean has(String field) {
        return exchanges.containsKey(field);
    }
}
