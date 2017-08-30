package com.benayn.constell.services.capricorn.repository.bean;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.RoleRepository;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample;
import com.benayn.constell.services.capricorn.repository.mapper.RoleMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryBean extends GenericRepository<Role, RoleExample, RoleMapper> implements RoleRepository {

}
