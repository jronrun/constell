package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.service.util.LZString.encodes;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.BASE_API_V1;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = MANAGE_BASE)
public class LoginController {

    @GetMapping("login")
    public void login(Model model) {
        model.addAttribute("info", encodes(ImmutableMap.of(
            "authorization", BASE_API_V1 + "/user/authorization",
            "redirect", MANAGE_BASE + "index"
        )));
    }

}
