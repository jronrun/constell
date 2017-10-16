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
    private List<DefinedOption> options;

    public DefinedElement clones() {
        DefinedElement el = new DefinedElement();

        el.setId(getId());
        el.setName(getName());
        el.setReadonly(getReadonly());
        el.setDisabled(getDisabled());

        el.setTag(getTag());
        el.setType(getType());
        el.setTitle(getTitle());
        el.setClazz(getClazz());
        el.setStyle(getStyle());
        el.setLabel(getLabel());
        el.setPlaceholder(getPlaceholder());
        el.setAttributes(getAttributes());
        el.setValue(getValue());
        el.setFragmentValue(isFragmentValue());
        el.setHidden(isHidden());
        el.setOptions(getOptions());

        return el;
    }
}
