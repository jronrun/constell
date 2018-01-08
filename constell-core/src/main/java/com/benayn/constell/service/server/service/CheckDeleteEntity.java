package com.benayn.constell.service.server.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class CheckDeleteEntity {

    private boolean deletable;
    private String undeletableMessage;

}
