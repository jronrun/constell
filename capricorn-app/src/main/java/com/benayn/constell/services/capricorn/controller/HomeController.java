package com.benayn.constell.services.capricorn.controller;

import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(value = "/")
    public String home(Model model) {
        homeIndex(model);
        return "index";
    }

    @GetMapping(value = "/index")
    public void index(Model model) {
        homeIndex(model);
    }

    private void homeIndex(Model model) {
        model.addAttribute("now", LocalDateTime.now());
    }

}
