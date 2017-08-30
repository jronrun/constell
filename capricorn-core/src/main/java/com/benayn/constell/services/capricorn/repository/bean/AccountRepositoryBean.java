package com.benayn.constell.services.capricorn.repository.bean;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.domain.AccountExample;
import com.benayn.constell.services.capricorn.repository.mapper.AccountMapper;
import com.benayn.constell.services.capricorn.repository.AccountRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryBean
    extends GenericRepository<Account, AccountExample, AccountMapper> implements AccountRepository {

}
