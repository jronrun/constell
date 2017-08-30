package com.benayn.constell.services.capricorn.controller;

import static com.benayn.constell.services.capricorn.settings.constant.Authorities.USER_RETRIEVE_PERMISSION;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.BASE_PATH_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.request.RegisterRequest;
import com.benayn.constell.services.capricorn.service.AccountService;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = BASE_PATH_V1 + "/user")
@Slf4j
public class AuthenticationController {

    @Autowired
    private AccountService userService;

    @RolesAllowed("ROLE_USER")
    @RequestMapping(value="/get", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> sampleGet22(){
        return new ResponseEntity<>(userService.getAccountDetails("test@test.com"), HttpStatus.CREATED);
    }

    @RequestMapping(value="", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @RolesAllowed("ROLE_TEST")
    public ResponseEntity<Account> sampleGet(){
        return new ResponseEntity<>(userService.getAccountDetails("test@test.com"), HttpStatus.CREATED);
    }

    @RequestMapping(value="/get1", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @PreAuthorize(USER_RETRIEVE_PERMISSION)
    public ResponseEntity<Account> sampleGet1(String email){

        return new ResponseEntity<>(userService.getAccountDetails("test@test.com"), HttpStatus.CREATED);
    }

    @RequestMapping(value="/register", method= POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> register(@Valid @RequestBody RegisterRequest request){
        log.info("signup " + request.toString());
//        if(errors.hasErrors()){
//            throw new InvalidRequestException("Email already exists", errors);
//        }

        Account created = null;//userService.create();
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

}
