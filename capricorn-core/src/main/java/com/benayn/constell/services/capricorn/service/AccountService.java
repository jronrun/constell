package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.menu.MenuBread;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.model.AccountDetails;
import com.benayn.constell.services.capricorn.repository.model.UserToken;
import com.benayn.constell.services.capricorn.viewobject.AccountVo;
import java.util.List;

public interface AccountService {

    UserToken login(String clientId, String clientSecret, String username, String password) throws ServiceException;

    AccountDetails getAccountDetails(String email);

    Account create(String email, String password, String name);

    List<MenuBread> getUserMenus(Long accountId, boolean fetchUnauthorized);

    Account selectById(long entityId);

    Page<Account> selectPageBy(AccountVo condition);

    int deleteById(Long entityId) throws ServiceException;

    int save(AccountVo entity) throws ServiceException;

}
