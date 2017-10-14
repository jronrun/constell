package com.benayn.constell.services.capricorn.repository.bean;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.AccountRepository;
import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.domain.AccountExample;
import com.benayn.constell.services.capricorn.repository.mapper.AccountMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryBean
    extends GenericRepository<Account, AccountExample, AccountMapper> implements AccountRepository {

    @Override
    public Account getByEmail(String email) {
        AccountExample example = new AccountExample();
        example.createCriteria().andEmailEqualTo(email);

        return selectOne(example);
    }
}
