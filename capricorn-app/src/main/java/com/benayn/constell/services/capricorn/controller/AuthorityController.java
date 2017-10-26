package com.benayn.constell.services.capricorn.controller;

import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.BASE_API_V1;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.service.server.respond.Responds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = BASE_API_V1 + "/authority")
@Slf4j
public class AuthorityController {

    @RequestMapping(value="/test", method= GET)
    public ResponseEntity<Message> sampleGet22(Authentication authentication){
        return Responds.success(authentication);
    }

}
