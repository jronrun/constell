package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.service.server.respond.Responds.success;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;
import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.service.server.menu.MenuGroup;
import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.service.util.Assets;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.service.AccountService;
import com.benayn.constell.services.capricorn.settings.constant.Menus;
import com.benayn.constell.services.capricorn.viewobject.AccountVO;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = MANAGE_BASE)
public class AccountController extends BaseManageController<AccountVO> {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @MenuCapability(value = Menus.ACCOUNT_MANAGE, parent = Menus.AUTHORIZATION)
    @PreAuthorize(Authorities.ACCOUNT_INDEX)
    @GetMapping("account/index")
    public String index(Authentication authentication, Model model,
        @RequestHeader(value = "touchable", required = false) Boolean touchable) {
        return genericIndex(authentication, model, touchable);
    }

    @PreAuthorize(Authorities.ACCOUNT_INDEX)
    @GetMapping("accounts")
    public String accounts(Authentication authentication, Model model, AccountVO condition) {
        return genericList(authentication, model, accountService.selectPageBy(condition), condition);
    }

    @PreAuthorize(Authorities.ACCOUNT_MENU_INDEX)
    @GetMapping("account/menu")
    public void index(Model model, @RequestHeader(value = "accountId", required = false) Long accountId) {
        List<MenuGroup> groups = accountService.getUserMenus(accountId, true);
        ofNullable(accountId).ifPresent(id -> {
            Account account = accountService.selectById(id);
            model.addAttribute("menuTitle",
                getMessage("render.account.menu.title", account.getUsername()));
        });

        model.addAttribute("groups", groups);
    }

    @PreAuthorize(Authorities.ACCOUNT_RETRIEVE)
    @GetMapping(value = "account/{entityId}")
    public String retrieve(Authentication authentication, Model model, @PathVariable("entityId") Long entityId) {
        Account item = null;
        if (entityId > 0) {
            item = accountService.selectById(entityId);
        }

        return genericEdit(authentication, model, item);
    }

    @PreAuthorize(Authorities.ACCOUNT_CREATE)
    @PostMapping(value = "account", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> create(@Valid @RequestBody AccountVO entity) throws ServiceException {
        Assets.checkNotBlank(entity.getPassword(), "{render.account.assets.password}");
        Assets.checkResults(entity.getPassword().equals(entity.getPassword2()),
            "{render.account.assets.password.confirm}");
        return success(accountService.save(entity));
    }

    @PreAuthorize(Authorities.ACCOUNT_UPDATE)
    @PutMapping(value = "account", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> update(@Valid @RequestBody AccountVO entity) throws ServiceException {
        return success(accountService.save(entity));
    }

    @PreAuthorize(Authorities.ACCOUNT_DELETE)
    @DeleteMapping(value = "account/{entityId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> delete(@PathVariable("entityId") Long entityId) throws ServiceException {
        return success(accountService.deleteById(entityId));
    }
}
