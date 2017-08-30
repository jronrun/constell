package com.benayn.constell.services.capricorn.repository.bean;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.PermissionRepository;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.PermissionExample;
import com.benayn.constell.services.capricorn.repository.mapper.PermissionMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionRepositoryBean
    extends GenericRepository<Permission, PermissionExample, PermissionMapper> implements PermissionRepository {

}
