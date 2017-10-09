package com.benayn.constell.service.server.respond;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;

@Getter
public class DefinedEditElement {

    private List<DefinedElement> hiddenGroup = Lists.newArrayList();
    private List<DefinedElement> wellGroup = Lists.newArrayList();
    private List<DefinedElement> rowGroup = Lists.newArrayList();

    public void addHiddenElement(DefinedElement element) {
        hiddenGroup.add(element);
    }

    public void addWellElement(DefinedElement element) {
        wellGroup.add(element);
    }

    public void addRowElement(DefinedElement element) {
        rowGroup.add(element);
    }

}
