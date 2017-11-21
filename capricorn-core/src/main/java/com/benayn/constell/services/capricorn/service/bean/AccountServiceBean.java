package com.benayn.constell.services.capricorn.service.bean;

import static com.benayn.constell.service.util.Assets.checkNotBlank;
import static com.benayn.constell.service.util.Assets.checkNotNull;
import static com.benayn.constell.service.util.Assets.checkRecordDeleted;
import static com.benayn.constell.service.util.Assets.checkRecordNoneExist;
import static com.benayn.constell.service.util.Assets.checkRecordSaved;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static java.lang.String.format;

import com.alibaba.fastjson.JSON;
import com.benayn.constell.service.enums.Gender;
import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.menu.AuthorityMenuBread;
import com.benayn.constell.service.server.menu.MenuBread;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.services.capricorn.enums.AccountStatus;
import com.benayn.constell.services.capricorn.repository.AccountRepository;
import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.domain.AccountExample;
import com.benayn.constell.services.capricorn.repository.domain.AccountExample.Criteria;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.model.AccountDetails;
import com.benayn.constell.services.capricorn.repository.model.UserToken;
import com.benayn.constell.services.capricorn.service.AccountService;
import com.benayn.constell.services.capricorn.service.AuthorityService;
import com.benayn.constell.services.capricorn.viewobject.AccountVo;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountServiceBean implements AccountService {

    @Value("${server.oauth.token.url}")
    private String oauthTokenUrl;

    private AccountRepository accountRepository;
    private AuthorityService authorityService;

    @Autowired
    public AccountServiceBean(AccountRepository accountRepository, AuthorityService authorityService) {
        this.accountRepository = accountRepository;
        this.authorityService = authorityService;
    }

    @Override
    public UserToken authorization(String clientId, String clientSecret, String username, String password) throws ServiceException {
        FormBody body = new FormBody.Builder()
            .add("grant_type", "password")
            .add("username", checkNotBlank(username, "{render.account.assets.username}"))
            .add("password", checkNotBlank(password, "{render.account.assets.password}"))
            .build();

        Request request = new Request.Builder().url(oauthTokenUrl)
            .header(AUTHORIZATION, format("Basic %s", base64().encode(format("%s:%s", clientId, clientSecret).getBytes())))
            .post(body)
            .build();

        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.isSuccessful()) {
                return JSON.parseObject(checkNotNull(
                    response.body(), "response body is null").string(), UserToken.class);
            }
        } catch (IOException e) {
            log.warn("{} login failed: {}", username, e.getMessage());
        }

        throw new ServiceException("{render.account.login.fail}");
    }

    @Override
    @Cacheable(value = "accounts", sync = true)
    public AccountDetails getAccountDetails(String email) {
        Account account = accountRepository.getByEmail(email);
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
        List<AuthorityMenuBread> authorityMenus = authorityService.getAuthorityMenus();

        List<Role> roles = authorityService.getRolesByAccountId(accountId);
        List<Permission> permissions = authorityService.getPermissionByAccountId(accountId);

        authenticateMenu(menus, authorityMenus, roles, permissions, fetchUnauthorized);

        return menus;
    }

    private void authenticateMenu(List<MenuBread> menus, List<AuthorityMenuBread> authorityMenus,
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

    private void asMenuitem(List<MenuBread> menus, AuthorityMenuBread authorityMenu,
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

        accountRepository.insertAll(user);
        return user;
    }

    @Override
    public Account selectById(long entityId) {
        return accountRepository.selectById(entityId);
    }

    @Override
    public Page<Account> selectPageBy(AccountVo condition) {
        AccountExample example = new AccountExample();
        Criteria criteria = example.createCriteria();

        if (null != condition.getId()) {
            criteria.andIdEqualTo(condition.getId());
        }

        if (null != condition.getUsername()) {
            criteria.andUsernameLike(condition.like(condition.getUsername()));
        }

        if (null != condition.getEmail()) {
            criteria.andEmailLike(condition.like(condition.getEmail()));
        }

        if (null != condition.getStatus()) {
            criteria.andStatusEqualTo(condition.getStatus());
        }

        if (condition.hasTouchOwner()) {
            List<Long> itemIds = Lists.newArrayList();
            switch (condition.getTouchModule()) {
                case "role":
                    itemIds = accountRepository
                        .getRoleOwnerIdsBy(condition.getTouchId(), null, condition.getPageNo(), condition.getPageSize());
                    break;
            }

            if (itemIds.size() < 1) {
                itemIds.add(-1L);
            }

            criteria.andIdIn(itemIds);
        }

        Page<Account> page = accountRepository.selectPageBy(example, condition.getPageNo(), condition.getPageSize());

        if (condition.hasTouch()) {
            List<Long> checkItemIds = page.getResource().stream()
                .map(Account::getId)
                .collect(Collectors.toList())
                ;

            List<Long> ownerIds = Lists.newArrayList();

            switch (condition.getTouchModule()) {
                case "role":
                    ownerIds = accountRepository
                        .getRoleOwnerIdsBy(condition.getTouchId(), checkItemIds, null, null);
                    break;
            }

            page.setAsTouchOwnerIds(ownerIds);
        }

        return page;
    }

    @Override
    public int deleteById(Long entityId) throws ServiceException {
        Account delete = new Account();
        delete.setId(entityId);
        delete.setStatus(AccountStatus.DELETED.getValue());
        return checkRecordDeleted(accountRepository.updateById(delete));
    }

    @Override
    public int save(AccountVo entity) throws ServiceException {
        Date now = new Date();

        Account item = new Account();
        item.setId(entity.getId());
        item.setEmail(entity.getEmail());
        item.setUsername(entity.getUsername());
        item.setGender(entity.getGender());
        item.setStatus(entity.getStatus());
        item.setCreateTime(now);
        item.setLastModifyTime(now);
        item.setEnabled(entity.isEnabled());
        item.setCredentialsExpired(entity.isCredentialsExpired());
        item.setExpired(entity.isExpired());
        item.setLocked(entity.isLocked());

        int result;
        Account savedAccount = accountRepository.getByEmail(entity.getEmail());

        // create
        if (null == item.getId()) {
            checkRecordNoneExist(null == savedAccount, item.getEmail());

            item.setPassword(entity.getPassword());
            item.setCreateTime(now);
            result = accountRepository.insertAll(item);
        }
        // update
        else {
            checkRecordNoneExist(null == savedAccount
                || savedAccount.getId().longValue() == item.getId(), item.getEmail());
            result = accountRepository.updateById(item);
        }

        return checkRecordSaved(result);
    }
}
