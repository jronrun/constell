package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.TouchRelation;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.viewobject.RoleVO;

public interface RoleService {

    Role selectById(long entityId);

    Page<Role> selectPageBy(RoleVO condition);

    int deleteById(Long entityId) throws ServiceException;

    int save(RoleVO entity) throws ServiceException;

    int createAccountRole(TouchRelation relation) throws ServiceException;

    int deleteAccountRole(TouchRelation relation) throws ServiceException;
}
