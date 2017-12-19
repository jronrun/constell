package com.benayn.constell.service.server.respond;

import lombok.Data;

@Data
public class DefinedTouch {

    private String id;
    // from module
    private String module;
    // from module id
    private String moduleId;

    private String name;
    private String touchHref;
    private boolean master;
    private String relationHref;
    private boolean switchable;

    private boolean hasActionField;
    private String actionField;

    private String titleFragment;
    private String cellFragment;
    private String touchFragment;

}
