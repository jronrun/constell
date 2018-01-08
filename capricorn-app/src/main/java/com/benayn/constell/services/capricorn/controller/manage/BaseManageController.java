package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.service.server.security.Authentications.getAuthorityNames;
import static com.benayn.constell.service.util.LZString.encodes;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.DEFINED_EDIT_KEY;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.DEFINED_PAGE_KEY;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.DEFINED_SEARCH_KEY;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.PAGE_EDIT;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.PAGE_INDEX;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.PAGE_LIST;
import static com.google.common.base.Strings.isNullOrEmpty;

import com.benayn.constell.service.server.component.ViewObjectResolver;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.DataExchange;
import com.benayn.constell.service.server.respond.DefinedAccess;
import com.benayn.constell.service.server.respond.PageInfo;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.services.capricorn.repository.model.CheckPermission;
import com.benayn.constell.services.capricorn.service.AuthorityService;
import com.google.common.reflect.TypeToken;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

public abstract class BaseManageController<T extends Renderable> {

    @Getter
    private ViewObjectResolver viewObjectResolver;
    @Getter
    private AuthorityService authorityService;

    @SuppressWarnings("unchecked")
    private final Class<T> voClass = ((Class<T>) new TypeToken<T>(getClass()) {}.getRawType());

    String genericIndex(Authentication authentication, Model model) {
        return genericIndex(authentication, model, null);
    }

    String genericIndex(Authentication authentication, Model model, Boolean touchable) {
        return genericIndex(authentication, model, touchable, null);
    }

    String genericIndex(Authentication authentication, Model model, Boolean touchable, Renderable searchValue) {
        addAccess(authentication, model);

        boolean isTouchable = null != touchable && touchable;
        PageInfo pageInfo = getViewObjectResolver().getPageInfo(voClass, MANAGE_BASE);

        model.addAttribute("title", pageInfo.getTitle());
        model.addAttribute("page", encodes(pageInfo));
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("touchable", isTouchable);
        model.addAttribute(DEFINED_SEARCH_KEY, getViewObjectResolver().getDefinedSearch(voClass, searchValue, null));

        return PAGE_INDEX;
    }

    String genericList(Authentication authentication, Model model, Page<?> page) {
        return genericList(authentication, model, page, null);
    }

    String genericList(Authentication authentication, Model model, Page<?> page, Renderable touchRenderable) {
        DefinedAccess definedAccess = addAccess(authentication, model);
        model.addAttribute("listInfo", encodes(page.cloneButResource()));
        model.addAttribute(DEFINED_PAGE_KEY, getViewObjectResolver()
            .getDefinedPage(voClass, page, MANAGE_BASE, touchRenderable, definedAccess));
        return PAGE_LIST;
    }

    String genericEdit(Authentication authentication, Model model, Object value) {
        return genericEdit(authentication, model, value, null);
    }

    String genericEdit(Authentication authentication, Model model, Object value, DataExchange dataExchange) {
        addAccess(authentication, model);
        PageInfo pageInfo = getViewObjectResolver().getPageInfo(voClass, MANAGE_BASE);

        // 1 create, 2 edit
        model.addAttribute("actionType", null == value ? 1 : 2);
        model.addAttribute("editId", pageInfo.getEditId());
        model.addAttribute(DEFINED_EDIT_KEY, getViewObjectResolver().getDefinedEdit(voClass, value, dataExchange));
        return PAGE_EDIT;
    }

    String getMessage(String code, Object... args) {
        return getViewObjectResolver().getMessage(code, code, null, args);
    }

    private DefinedAccess addAccess(Authentication authentication, Model model) {
        DefinedAccess access = getDefinedAccess(authentication);
        model.addAttribute("access", access);
        return access;
    }

    private DefinedAccess getDefinedAccess(Authentication authentication) {
        return getViewObjectResolver().getDefinedAccess(voClass, true,
            (String permission) -> authorityService.authenticate(permission, getAuthorityNames(authentication),
            (CheckPermission check) -> {
                String name = check.getPermissionName();
                String code = check.getPermission().getCode();
                return !isNullOrEmpty(name) && name.contains("hasPermission") && name.contains(code);
            }
        ));
    }

    @Autowired
    public void initialize(ViewObjectResolver viewObjectResolver, AuthorityService authorityService) {
        this.viewObjectResolver = viewObjectResolver;
        this.authorityService = authorityService;
    }

}
