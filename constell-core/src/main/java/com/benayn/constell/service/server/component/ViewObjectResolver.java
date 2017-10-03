package com.benayn.constell.service.server.component;

import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.DefinedElement;
import com.benayn.constell.service.server.respond.Renderable;
import java.util.List;
import java.util.Locale;

public interface ViewObjectResolver {

    List<DefinedElement> getDefinedSearch(Class<? extends Renderable> viewObjectType, Object value);

    List<DefinedElement> getDefinedEdit(Class<? extends Renderable> viewObjectType, Object value);

    Page<Renderable> getDefinedPage(Class<? extends Renderable> viewObjectType, Page<?> page);

    String getMessage(String code, String defaultMessage, Object... args);
    String getMessage(String code, String defaultMessage, Locale locale, Object... args);
}
