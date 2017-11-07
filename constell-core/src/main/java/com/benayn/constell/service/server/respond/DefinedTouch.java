package com.benayn.constell.service.server.respond;

import lombok.Data;

@Data
public class DefinedTouch {

    private String id;
    private String module;
    private String name;
    private TouchType touchType;
    private String touchHref;

    private boolean hasActionField;
    private String actionField;

    private String titleFragment;
    private String cellFragment;

}
