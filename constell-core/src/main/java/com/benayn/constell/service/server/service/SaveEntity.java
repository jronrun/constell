package com.benayn.constell.service.server.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class SaveEntity<T> {

    private boolean isCreate;
    private String primaryKey;
    private T entity;

}
