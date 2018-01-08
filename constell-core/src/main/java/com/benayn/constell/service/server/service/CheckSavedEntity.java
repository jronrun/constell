package com.benayn.constell.service.server.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class CheckSavedEntity<T> {

    private T savedEntity;
    private String checkedValue;

}
