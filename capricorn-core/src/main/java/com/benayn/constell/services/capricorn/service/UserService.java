package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.services.capricorn.domain.User;
import com.benayn.constell.service.enums.Gender;

public interface UserService {

    User findByEmail(String email);

    User create(String email, String password, String name, Gender gender);

}
