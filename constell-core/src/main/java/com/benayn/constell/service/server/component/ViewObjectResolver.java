package com.benayn.constell.service.server.component;

import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.DataExchange;
import com.benayn.constell.service.server.respond.DefinedAccess;
import com.benayn.constell.service.server.respond.DefinedEditElement;
import com.benayn.constell.service.server.respond.DefinedElement;
import com.benayn.constell.service.server.respond.PageInfo;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.service.SaveEntity;
import com.benayn.constell.service.server.service.SearchEntity;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public interface ViewObjectResolver {

    List<DefinedElement> getDefinedSearch(Class<? extends Renderable> viewObjectType,
        Object value, DataExchange dataExchange);

    DefinedEditElement getDefinedEdit(Class<? extends Renderable> viewObjectType,
        Object value, DataExchange dataExchange);

    Page<Renderable> getDefinedPage(Class<? extends Renderable> viewObjectType, Page<?> page,
        String manageBaseUrl, Renderable touchRenderable, DefinedAccess definedAccess);

    String getMessage(String code, String defaultMessage, Object... args);
    String getMessage(String code, String defaultMessage, Locale locale, Object... args);

    <T extends Renderable> PageInfo getPageInfo(Class<T> viewObjectType, String manageBaseUrl);
    DefinedAccess getDefinedAccess(Class<? extends Renderable> viewObjectType,
        boolean denyAllIfNoneDefine, Predicate<String> predicate);

    <T> SaveEntity<T> getSaveEntity(Renderable renderable, T entity);

    SearchEntity getSearchEntity(Renderable condition);
}
