package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.services.capricorn.repository.domain.Role;
import java.util.List;

public interface AuthorityService {

    boolean authenticate(String permission, List<String> authorities);

    List<Role> getRolesByAccountId(Long accountId);
}
