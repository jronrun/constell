package com.benayn.constell.services.capricorn.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping(value = "/", method= GET)
    public String home() {
        return "Hello World";
    }

    @RequestMapping(value = "/test", method= GET)
    public String test() {
        return "test World";
    }

}
