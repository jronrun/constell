package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.service.server.respond.Responds.success;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.util.StringUtils.parseLocaleString;

import com.benayn.constell.service.server.menu.MenuGroup;
import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.service.server.security.ConstellationUserDetails;
import com.benayn.constell.services.capricorn.service.AccountService;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
@RequestMapping(value = MANAGE_BASE)
public class ManageController {

    private AccountService accountService;

    @GetMapping(value = "index")
    public void index(Model model) {
        model.addAttribute("now", LocalDateTime.now());
    }

    @GetMapping(value = "side-menu")
    public void sideMenu(Model model, Authentication authentication) {
        addMenuData(model, authentication);
    }

    @GetMapping(value = "side-menu-small")
    public void sideSmallView(Model model, Authentication authentication) {
        addMenuData(model, authentication);
    }

    @GetMapping(value = "navigation")
    public void navigation(Model model) {
    }

    @GetMapping(value = "navigation-small")
    public void navigationSmall(Model model) {
    }

    @GetMapping(value = "thin-menu")
    public void thinMenu(Model model, Authentication authentication) {
        addMenuData(model, authentication);
    }

    @GetMapping(value = "footer")
    public void footer(Model model) {
    }

    @GetMapping(value = "language", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> getLanguage() {
        return success(LocaleContextHolder.getLocale().toString());
    }

    @PostMapping(value = "language/{lang}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> setLanguage(@PathVariable("lang") String lang, HttpServletRequest request,
        HttpServletResponse response) {

        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver == null) {
            throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
        }

        localeResolver.setLocale(request, response, parseLocaleString(lang));
        return success(null);
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    private void addMenuData(Model model, Authentication authentication) {
        ConstellationUserDetails user = (ConstellationUserDetails) authentication.getPrincipal();

        //TODO rem
        List<MenuGroup> groups = accountService.getUserMenus(user.getId(), true);
//        List<MenuGroup> groups = accountService.getUserMenus(user.getId(), false);
        model.addAttribute("groups", groups);

        String firstGroupTitle = "";
        if (groups.size() > 0) {
            firstGroupTitle = groups.get(0).getTitle();
        }

        model.addAttribute("firstGroupTitle", firstGroupTitle);
    }
}
