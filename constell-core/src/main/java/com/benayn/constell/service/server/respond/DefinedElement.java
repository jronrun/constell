package com.benayn.constell.service.server.respond;

import static com.benayn.constell.service.server.respond.HtmlTag.TEXTAREA;
import static com.benayn.constell.service.server.respond.HtmlTag.VIEW_OBJECT;

import com.benayn.constell.service.common.Pair;
import java.util.List;
import lombok.Data;

@Data
public class DefinedElement {

    private HtmlTag tag;
    private String tagName;
    private String id;
    private String name;
    private InputType type;
    private String typeName;
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

    public boolean isRowElement() {
        return TEXTAREA == getTag() || VIEW_OBJECT == getTag();
    }

    public DefinedElement clones() {
        DefinedElement el = new DefinedElement();

        el.setId(getId());
        el.setName(getName());
        el.setReadonly(getReadonly());
        el.setDisabled(getDisabled());

        el.setTag(getTag());
        el.setTagName(getTagName());
        el.setType(getType());
        el.setTypeName(getTypeName());
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
