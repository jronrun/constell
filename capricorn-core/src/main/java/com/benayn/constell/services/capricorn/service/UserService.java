package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.services.capricorn.domain.User;

public interface UserService {

    User findByEmail(String email);

    User create(String email, String password, String name);

}
