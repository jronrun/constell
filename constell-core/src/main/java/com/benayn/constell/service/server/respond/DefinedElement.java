package com.benayn.constell.service.server.respond;

import com.benayn.constell.service.common.Pair;
import java.util.List;
import lombok.Data;

@Data
public class DefinedElement {

    private HtmlTag tag;
    private String id;
    private String name;
    private InputType type;
    private String title;
    private String clazz;
    private String style;
    private Boolean readonly;
    private Boolean disabled;
    private String label;
    private String placeholder;
    private List<Pair<String, String>> attributes;
    private Object value;
    private boolean fragmentValue;
    private boolean hidden;

}
