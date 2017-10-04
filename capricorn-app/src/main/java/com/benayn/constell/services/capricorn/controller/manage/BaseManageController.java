package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.DEFINED_EDIT_KEY;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.DEFINED_LIST_KEY;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.DEFINED_SEARCH_KEY;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.PAGE_EDIT;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.PAGE_INDEX;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.PAGE_LIST;

import com.benayn.constell.service.server.component.ViewObjectResolver;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.Renderable;
import com.google.common.reflect.TypeToken;
import java.util.Locale;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

public abstract class BaseManageController<T extends Renderable> {

    @Getter
    private ViewObjectResolver viewObjectResolver;

    @SuppressWarnings("unchecked")
    private final Class<T> voClass = ((Class<T>) new TypeToken<T>(getClass()) {}.getRawType());

    String genericIndex(Model model) {
        String moduleName = voClass.getSimpleName().toLowerCase();
        moduleName = moduleName.substring(0, moduleName.length() - 2);
        model.addAttribute("title", viewObjectResolver.getMessage(
            String.format("render.%s.module.title", moduleName), null));

        model.addAttribute(DEFINED_SEARCH_KEY, viewObjectResolver.getDefinedSearch(voClass, null));
        return PAGE_INDEX;
    }

    String genericList(Model model, Page<?> page) {
        model.addAttribute(DEFINED_LIST_KEY, viewObjectResolver.getDefinedPage(voClass, page));
        return PAGE_LIST;
    }

    String genericEdit(Model model, Object value) {
        model.addAttribute(DEFINED_EDIT_KEY, viewObjectResolver.getDefinedEdit(voClass, value));
        return PAGE_EDIT;
    }

    String getMessage(String code, String defaultMessage, Object... args) {
        return viewObjectResolver.getMessage(code, defaultMessage, args);
    }

    String getMessage(String code, String defaultMessage, Locale locale, Object... args) {
        return viewObjectResolver.getMessage(code, defaultMessage, locale, args);
    }

    @Autowired
    public void initialize(ViewObjectResolver viewObjectResolver) {
        this.viewObjectResolver = viewObjectResolver;
    }

}
