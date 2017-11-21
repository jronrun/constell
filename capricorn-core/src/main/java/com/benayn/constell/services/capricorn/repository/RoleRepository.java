package com.benayn.constell.services.capricorn.repository;

import com.benayn.constell.service.server.repository.Repository;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample;
import java.util.List;

public interface RoleRepository extends Repository<Role, RoleExample> {

    List<Role> getByAccountId(Long accountId);

    Role getByCode(String code);

    int saveAccountRole(List<Long> accountIds, List<Long> roleIds);

    int deleteAccountRole(List<Long> accountIds, List<Long> roleIds);

    List<Long> getAccountOwnerIdsBy(Long accountId, List<Long> roleIds, Integer pageNo, Integer pageSize);

    List<Long> getPermissionOwnerIdsBy(Long permissionId, List<Long> roleIds, Integer pageNo, Integer pageSize);
}
