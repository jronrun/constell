package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.services.capricorn.condition.RoleCondition;
import com.benayn.constell.services.capricorn.repository.domain.Role;

public interface RoleService {

    Role selectById(long entityId);

    Page<Role> selectPageBy(RoleCondition condition);
}
