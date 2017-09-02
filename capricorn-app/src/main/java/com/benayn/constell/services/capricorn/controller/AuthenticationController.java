package com.benayn.constell.services.capricorn.controller;

import static com.benayn.constell.services.capricorn.settings.constant.Authorities.USER_CREATE_PERMISSION;
import static com.benayn.constell.services.capricorn.settings.constant.Authorities.USER_RETRIEVE_PERMISSION;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.BASE_PATH_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.request.RegisterRequest;
import com.benayn.constell.services.capricorn.service.AccountService;
import com.benayn.constell.services.capricorn.settings.menu.MenuCapability;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = BASE_PATH_V1 + "/user")
@Slf4j
public class AuthenticationController {

    @Autowired
    private AccountService userService;

    @MenuCapability("系统菜单")
    @RolesAllowed("ROLE_USER")
    @RequestMapping(value="/get", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> sampleGet22(){
        return new ResponseEntity<>(userService.getAccountDetails("test@test.com"), HttpStatus.CREATED);
    }



    @MenuCapability(value = "权限管理", parent = "系统菜单")
    @RequestMapping(value="", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @RolesAllowed("ROLE_TEST")
    public ResponseEntity<Account> sampleGet(){
        return new ResponseEntity<>(userService.getAccountDetails("test@test.com"), HttpStatus.CREATED);
    }

    @MenuCapability(value = "角色管理", parent = "系统菜单", order = 10)
    @RequestMapping(value="/get1", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @PreAuthorize(USER_RETRIEVE_PERMISSION)
    public ResponseEntity<Account> sampleGet1(String email){

        return new ResponseEntity<>(userService.getAccountDetails("test@test.com"), HttpStatus.CREATED);
    }

    @MenuCapability(value = "丝瓜管理", parent = "系统菜单")
    @RequestMapping(value="/gets/{email}", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @PreAuthorize(USER_RETRIEVE_PERMISSION)
    public ResponseEntity<Account> getsee(@PathVariable String email){
        return new ResponseEntity<>(userService.getAccountDetails(email), HttpStatus.CREATED);
    }

    @MenuCapability(value = "角色添加", parent = "角色管理")
    @RequestMapping(value="/register", method= POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> register(@Valid @RequestBody RegisterRequest request){
        log.info("signup " + request.toString());
//        if(errors.hasErrors()){
//            throw new InvalidRequestException("Email already exists", errors);
//        }

        Account created = null;//userService.create();
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @MenuCapability(value = "丝瓜管理1", parent = "系统菜单", order = 1)
    @RequestMapping(value="/gets/test1", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public void test1(){

    }

    @MenuCapability(value = "顶级菜单", order = 10)
    @PreAuthorize(USER_CREATE_PERMISSION)
    @RequestMapping(value="/gets/test2", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public void test2(){

    }

}
