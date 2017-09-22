package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;

import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = MANAGE_BASE)
public class ManageController {

    @GetMapping(value = "index")
    public void index(Model model) {
        model.addAttribute("now", LocalDateTime.now());
    }

    @GetMapping(value = "side-menu")
    public void sideMenu(Model model) {
    }

    @GetMapping(value = "side-small-view")
    public void sideSmallView(Model model) {
    }

    @GetMapping(value = "navigation")
    public void navigation(Model model) {
    }

    @GetMapping(value = "navigation-small")
    public void navigationSmall(Model model) {
    }

    @GetMapping(value = "thin-menu")
    public void thinMenu(Model model) {
    }

    @GetMapping(value = "footer")
    public void footer(Model model) {
    }

}
