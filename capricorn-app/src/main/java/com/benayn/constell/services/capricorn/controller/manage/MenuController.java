package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.service.server.security.Authentications.getUserId;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;

import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.service.server.menu.MenuGroup;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.service.AccountService;
import com.benayn.constell.services.capricorn.settings.constant.Menus;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = MANAGE_BASE)
public class MenuController {

    private AccountService accountService;

    @Autowired
    public MenuController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize(Authorities.MENU_RETRIEVE)
    @GetMapping("menu/index")
    public void index(Model model, Authentication authentication) {
        List<MenuGroup> groups = accountService.getUserMenus(getUserId(authentication), true);
        model.addAttribute("groups", groups);
    }

    @RolesAllowed(Authorities.ROLE_MANAGE)
    @MenuCapability(value = Menus.AUTHORIZATION, group = Menus.GROUP_CAPRICORN)
    @GetMapping(value = "menu/authorization")
    public void menuAuthorization() {

    }

    @RolesAllowed(Authorities.ROLE_MANAGE)
    @MenuCapability(value = Menus.SETTINGS, group = Menus.GROUP_CAPRICORN)
    @GetMapping(value = "menu/settings")
    public void menuSettings() {

    }

    @RolesAllowed(Authorities.ROLE_MANAGE)
    @MenuCapability(value = "Test Help", group = Menus.GROUP_HELP)
    @GetMapping(value = "menu/help")
    public void testHelp() {

    }

}
