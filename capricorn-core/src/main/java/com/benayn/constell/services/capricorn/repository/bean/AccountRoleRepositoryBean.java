package com.benayn.constell.services.capricorn.repository.bean;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.AccountRoleRepository;
import com.benayn.constell.services.capricorn.repository.domain.AccountRole;
import com.benayn.constell.services.capricorn.repository.domain.AccountRoleExample;
import com.benayn.constell.services.capricorn.repository.mapper.AccountRoleMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRoleRepositoryBean extends
    GenericRepository<AccountRole, AccountRoleExample, AccountRoleMapper> implements AccountRoleRepository {

}
