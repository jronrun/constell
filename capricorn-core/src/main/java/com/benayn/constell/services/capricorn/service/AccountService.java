package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.service.server.menu.MenuBread;
import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.model.AccountDetails;
import java.util.List;

public interface AccountService {

    AccountDetails getAccountDetails(String email);

    Account create(String email, String password, String name);

    List<MenuBread> getUserMenus(Long accountId, boolean fetchUnauthorized);

}
