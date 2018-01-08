package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.TouchRelation;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.viewobject.PermissionVO;

public interface PermissionService {

    Permission selectById(long entityId);

    Page<Permission> selectPageBy(PermissionVO condition);

    int save(PermissionVO entity) throws ServiceException;

    int deleteById(Long entityId) throws ServiceException;

    boolean saveFromAuthorities();

    int createRolePermission(TouchRelation relation) throws ServiceException;
    int deleteRolePermission(TouchRelation relation) throws ServiceException;
}
