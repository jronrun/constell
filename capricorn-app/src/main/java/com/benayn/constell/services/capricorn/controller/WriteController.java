package com.benayn.constell.services.capricorn.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class WriteController {

    @GetMapping(value = "/write")
    public void write(Model model) {
        System.out.println('h');
    }

    @GetMapping(value = "/show")
    public void show(Model model) {
        System.out.println('s');
    }

    @GetMapping(value = "/diff")
    public void diff(Model model) {
        System.out.println('d');
    }

    @GetMapping(value = "/rich")
    public void rich(Model model) {
        System.out.println('r');
    }

}
