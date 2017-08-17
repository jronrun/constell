package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.services.capricorn.repository.domain.Account;

public interface AccountService {

    Account findByEmail(String email);

    Account create(String email, String password, String name);

}
