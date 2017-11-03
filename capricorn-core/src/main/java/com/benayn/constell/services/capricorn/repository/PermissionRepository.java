package com.benayn.constell.services.capricorn.repository;

import com.benayn.constell.service.server.repository.Repository;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.PermissionExample;
import java.util.List;

public interface PermissionRepository extends Repository<Permission, PermissionExample> {

    List<Permission> getByRoleId(Long roleId);

    Permission getByCode(String code);

    List<Long> getOwnerIdsBy(Long roleId, List<Long> permissionIds, Integer pageNo, Integer pageSize);

}
