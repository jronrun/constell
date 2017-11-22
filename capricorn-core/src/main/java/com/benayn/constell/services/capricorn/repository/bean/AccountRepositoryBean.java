package com.benayn.constell.services.capricorn.repository.bean;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.enums.CacheName;
import com.benayn.constell.services.capricorn.repository.AccountRepository;
import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.domain.AccountExample;
import com.benayn.constell.services.capricorn.repository.domain.AccountRole;
import com.benayn.constell.services.capricorn.repository.domain.AccountRoleExample;
import com.benayn.constell.services.capricorn.repository.domain.AccountRoleExample.Criteria;
import com.benayn.constell.services.capricorn.repository.mapper.AccountMapper;
import com.benayn.constell.services.capricorn.repository.mapper.AccountRoleMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = CacheName.ACCOUNTS)
public class AccountRepositoryBean
    extends GenericRepository<Account, AccountExample, AccountMapper> implements AccountRepository {

    private AccountRoleMapper accountRoleMapper;

    @Override
    @Cacheable(sync = true)
    public Account getByEmail(String email) {
        AccountExample example = new AccountExample();
        example.createCriteria().andEmailEqualTo(email);

        return selectOne(example);
    }

    @Override
    public List<Long> getRoleOwnerIdsBy(Long roleId, List<Long> accountIds, Integer pageNo, Integer pageSize) {
        AccountRoleExample accountRoleExample = new AccountRoleExample();
        Criteria criteria = accountRoleExample.createCriteria().andRoleIdEqualTo(roleId);

        if (null != accountIds && accountIds.size() > 0) {
            criteria.andAccountIdIn(accountIds);
        }

        if (null != pageNo && null != pageSize) {
            addPageFeature(accountRoleExample, pageNo, pageSize);
        }

        List<AccountRole> rolePermissions = accountRoleMapper.selectByExample(accountRoleExample);

        return ofNullable(rolePermissions).orElse(newArrayList())
            .stream()
            .map(AccountRole::getAccountId)
            .collect(Collectors.toList())
            ;
    }

    @Override
    protected void setup() {
        this.accountRoleMapper = getMapper(AccountRoleMapper.class);
    }
}
