package com.benayn.constell.service.server.respond;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class DefinedOption {

    private String label;
    private Object value;
    private boolean chosen;

}
