package com.benayn.constell.services.capricorn.repository;

import com.benayn.constell.service.server.repository.Repository;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample;
import java.util.List;

public interface RoleRepository extends Repository<Role, RoleExample> {

    List<Role> getByAccountId(Long accountId);

    Role getByCode(String code);
}
