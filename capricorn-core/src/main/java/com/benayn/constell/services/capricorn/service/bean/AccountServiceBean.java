package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.service.enums.Gender;
import com.benayn.constell.service.server.menu.AuthorityMenuitem;
import com.benayn.constell.service.server.menu.MenuBread;
import com.benayn.constell.services.capricorn.enums.AccountStatus;
import com.benayn.constell.services.capricorn.repository.AccountRepository;
import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.domain.AccountExample;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.model.AccountDetails;
import com.benayn.constell.services.capricorn.service.AccountService;
import com.benayn.constell.services.capricorn.service.AuthorityService;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceBean implements AccountService {

    @Autowired
    private AccountRepository userRepository;
    @Autowired
    private AuthorityService authorityService;

    @Override
    @Cacheable(value = "accounts", sync = true)
    public AccountDetails getAccountDetails(String email) {
        AccountExample example = new AccountExample();
        example.createCriteria().andEmailEqualTo(email);

        Account account = userRepository.selectOne(example);
        if (null == account) {
            return null;
        }

        List<Role> roles = authorityService.getRolesByAccountId(account.getId());
        return new AccountDetails(account, roles);
    }

    @Override
    @Cacheable("menus")
    public List<MenuBread> getUserMenus(Long accountId, boolean fetchUnauthorized) {
        List<MenuBread> menus = Lists.newArrayList();
        List<AuthorityMenuitem> authorityMenus = authorityService.getAuthorityMenus();

        List<Role> roles = authorityService.getRolesByAccountId(accountId);
        List<Permission> permissions = authorityService.getPermissionByAccountId(accountId);

        authenticateMenu(menus, authorityMenus, roles, permissions, fetchUnauthorized);

        return menus;
    }

    private void authenticateMenu(List<MenuBread> menus, List<AuthorityMenuitem> authorityMenus,
        List<Role> roles, List<Permission> permissions, boolean fetchUnauthorized) {

        authorityMenus.forEach(authorityMenu -> {
            boolean alreadyAdd = false;
            //check role
            if (null != authorityMenu.getRole()) {
                if (roles.stream()
                    .anyMatch(role -> authorityMenu.getRole().equals(role.getCode()))) {
                    asMenuitem(menus, authorityMenu, roles, permissions, fetchUnauthorized, true);
                    alreadyAdd = true;
                }
            }
            //check permission
            else if (null != authorityMenu.getAuthority()) {
                if (permissions.stream()
                    .anyMatch(permission -> authorityMenu.getAuthority().equals(permission.getCode()))) {
                    asMenuitem(menus, authorityMenu, roles, permissions, fetchUnauthorized, true);
                    alreadyAdd = true;
                }
            }

            if (!alreadyAdd && fetchUnauthorized) {
                asMenuitem(menus, authorityMenu, roles, permissions, true, false);
            }
        });
    }

    private void asMenuitem(List<MenuBread> menus, AuthorityMenuitem authorityMenu,
        List<Role> roles, List<Permission> permissions, boolean fetchUnauthorized, boolean authorized) {
        MenuBread menu = authorityMenu.asMenu(authorized);

        if (authorityMenu.hasChild()) {
            authenticateMenu(menu.getChildren(),
                authorityMenu.getChild(), roles, permissions, fetchUnauthorized);
        }

        menus.add(menu);
    }

    @Override
    public Account create(String email, String password, String name) {
        Account user = new Account();
        Date now = new Date();

        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(name);
        user.setGender(Gender.UNKNOWN.getValue());
        user.setStatus(AccountStatus.USING.getValue());
        user.setCreateTime(now);
        user.setLastModifyTime(now);
        user.setEnabled(true);

        userRepository.insertAll(user);
        return user;
    }

}
