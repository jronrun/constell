package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;

import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.settings.constant.Menus;
import javax.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = MANAGE_BASE)
public class MenuController {

    @RolesAllowed(Authorities.MENU_AUTHORIZATION)
    @MenuCapability(value = Menus.AUTHORIZATION, group = Menus.GROUP_CAPRICORN)
    @GetMapping(value = "menu/authorization")
    public void menuAuthorization() {

    }

    @RolesAllowed(Authorities.MENU_SETTINGS)
    @MenuCapability(value = Menus.SETTINGS, group = Menus.GROUP_CAPRICORN)
    @GetMapping(value = "menu/settings")
    public void menuSettings() {

    }

    @RolesAllowed(Authorities.ROLE_MANAGE)
    @MenuCapability(value = Menus.HELP, group = Menus.GROUP_HELP, groupOrder = 20)
    @GetMapping(value = "menu/help")
    public void menuHelp() {

    }

}
