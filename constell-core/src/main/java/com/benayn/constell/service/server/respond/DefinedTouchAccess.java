package com.benayn.constell.service.server.respond;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class DefinedTouchAccess {

    private String id;
    private boolean hasIndex;
    private boolean hasCreate;
    private boolean hasDelete;

}
