package com.benayn.constell.service.server.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Sorting {

    ASCENDING("asc"),
    DESCENDING("desc");

    @Getter
    private final String sort;

}
