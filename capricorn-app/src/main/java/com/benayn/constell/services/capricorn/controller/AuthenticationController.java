package com.benayn.constell.services.capricorn.controller;

import static com.benayn.constell.service.server.respond.Responds.success;
import static com.benayn.constell.service.util.Assets.checkNotBlank;
import static com.benayn.constell.service.util.Assets.checkNotNull;
import static com.benayn.constell.service.util.LZString.decodesAsMap;
import static com.benayn.constell.service.util.LZString.encodes;
import static com.benayn.constell.services.capricorn.config.Authorities.ROLE_ANONYMOUS;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.BASE_API_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.services.capricorn.repository.model.UserToken;
import com.benayn.constell.services.capricorn.service.AccountService;
import com.benayn.constell.services.capricorn.settings.config.CapricornAppConfiguration.CapricornConfigurer;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = BASE_API_V1 + "/user")
@Slf4j
public class AuthenticationController {

    private AccountService accountService;
    private CapricornConfigurer configurer;

    @Autowired
    public AuthenticationController(AccountService accountService, CapricornConfigurer configurer) {
        this.accountService = accountService;
        this.configurer = configurer;
    }

    @RolesAllowed(ROLE_ANONYMOUS)
    @PostMapping(value = "/authorization", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> authorization(@RequestBody String credentials, HttpServletResponse response) throws ServiceException {
        Map<String, Object> authentic = checkNotNull(decodesAsMap(checkNotBlank(credentials)));
        String username = checkNotBlank(checkNotNull(authentic.get("username")).toString());
        String password = checkNotBlank(checkNotNull(authentic.get("password")).toString());

        UserToken token = accountService.authorization(configurer.getClientId(), configurer.getClientSecret(), username, password);

        Cookie cookie = new Cookie("connect.credentials", encodes(token.getAccessToken()));
        cookie.setPath("/");
        cookie.setMaxAge(12 * 60 * 60);
        response.addCookie(cookie);

        return success(ImmutableMap.of("token", Hashing.sha256().hashLong(System.currentTimeMillis()).toString()));
    }

    /*



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
    //@PreAuthorize(USER_RETRIEVE_PERMISSION)
//    @PreAuthorize(USER_DELETE_PERMISSION)
    public ResponseEntity<Account> sampleGet1(String email){

        return new ResponseEntity<>(userService.getAccountDetails("test@test.com"), HttpStatus.CREATED);
    }

    @MenuCapability(value = "丝瓜管理", parent = "系统菜单")
    @RequestMapping(value="/gets/{email}", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    //@PreAuthorize(USER_RETRIEVE_PERMISSION)
    public ResponseEntity<Account> getsee(@PathVariable String email){
        return new ResponseEntity<>(userService.getAccountDetails(email), HttpStatus.CREATED);
    }

    @MenuCapability(value = "角色添加", parent = "角色管理")
    //@PreAuthorize(USER_RETRIEVE_PERMISSION)
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
//    @PreAuthorize(USER_CREATE_PERMISSION)
    @RequestMapping(value="/gets/test2", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public void test2(){

    }

    @RequestMapping(value="/menus", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuthorityMenuBread>> menus(){
        return new ResponseEntity<>(authorityService.getAuthorityMenus(), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value="/usermenu", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MenuBread>> usermenu(){
        return new ResponseEntity<>(userService.getUserMenus(24L, true), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value="/usermenuless", method= GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MenuBread>> usermenuless(){
        return new ResponseEntity<>(userService.getUserMenus(24L, false), HttpStatus.ACCEPTED);
    }
     */

}
