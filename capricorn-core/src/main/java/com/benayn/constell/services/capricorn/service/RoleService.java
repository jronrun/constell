package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.viewobject.RoleVo;

public interface RoleService {

    Role selectById(long entityId);

    Page<Role> selectPageBy(RoleVo condition);
}
