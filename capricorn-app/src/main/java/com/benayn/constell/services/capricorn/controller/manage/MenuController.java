package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;

import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.services.capricorn.settings.constant.Menus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = MANAGE_BASE)
public class MenuController {

    @MenuCapability(value = Menus.AUTHORIZATION, group = Menus.GROUP_CAPRICORN)
    @GetMapping(value = "menu/authorization")
    public void menuAuthorization() {

    }

    @MenuCapability(value = Menus.SETTINGS, group = Menus.GROUP_CAPRICORN)
    @GetMapping(value = "menu/settings")
    public void menuSettings() {

    }

    @MenuCapability(value = "Test Help", group = Menus.GROUP_HELP)
    @GetMapping(value = "menu/help")
    public void testHelp() {

    }

}
