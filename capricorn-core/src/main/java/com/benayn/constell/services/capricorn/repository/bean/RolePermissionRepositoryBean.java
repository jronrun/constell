package com.benayn.constell.services.capricorn.repository.bean;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.RolePermissionRepository;
import com.benayn.constell.services.capricorn.repository.domain.RolePermission;
import com.benayn.constell.services.capricorn.repository.domain.RolePermissionExample;
import com.benayn.constell.services.capricorn.repository.mapper.RolePermissionMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RolePermissionRepositoryBean
    extends GenericRepository<RolePermission, RolePermissionExample, RolePermissionMapper> implements RolePermissionRepository {

}
