package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.model.AccountDetails;

public interface AccountService {

    AccountDetails getAccountDetails(String email);

    Account create(String email, String password, String name);

}
