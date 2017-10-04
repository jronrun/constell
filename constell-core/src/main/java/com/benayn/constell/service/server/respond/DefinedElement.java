package com.benayn.constell.service.server.respond;

import lombok.Data;

@Data
public class DefinedElement {

    private TagName tag;
    private String id;
    private String name;
    private InputType type;
    private String title;
    private String clazz;
    private String style;
    private String label;
    private String placeholder;
    private String attributes;
    private Object value;

}
