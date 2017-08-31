package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.service.enums.Gender;
import com.benayn.constell.services.capricorn.enums.AccountStatus;
import com.benayn.constell.services.capricorn.repository.AccountRepository;
import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.domain.AccountExample;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.model.AccountDetails;
import com.benayn.constell.services.capricorn.service.AccountService;
import com.benayn.constell.services.capricorn.service.AuthorityService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceBean implements AccountService {

    @Autowired
    private AccountRepository userRepository;
    @Autowired
    private AuthorityService authorityService;

    @Override
    @Cacheable(value = "accounts", sync = true)
    public AccountDetails getAccountDetails(String email) {
        AccountExample example = new AccountExample();
        example.createCriteria().andEmailEqualTo(email);

        Account account = userRepository.selectOne(example);
        if (null == account) {
            return null;
        }

        List<Role> roles = authorityService.getRolesByAccountId(account.getId());
        return new AccountDetails(account, roles);
    }

    @Override
    public Account create(String email, String password, String name) {
        Account user = new Account();
        Date now = new Date();

        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(name);
        user.setGender(Gender.UNKNOWN.getValue());
        user.setStatus(AccountStatus.USING.getValue());
        user.setCreateTime(now);
        user.setLastModifyTime(now);
        user.setEnabled(true);

        userRepository.insertAll(user);
        return user;
    }

}
