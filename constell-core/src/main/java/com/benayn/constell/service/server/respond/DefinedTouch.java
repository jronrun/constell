package com.benayn.constell.service.server.respond;

import lombok.Data;

@Data
public class DefinedTouch {

    private String id;
    private String module;
    private String name;
    private String touchHref;
    private String relationHref;

    private boolean hasActionField;
    private String actionField;

    private String titleFragment;
    private String cellFragment;
    private String touchFragment;

}
