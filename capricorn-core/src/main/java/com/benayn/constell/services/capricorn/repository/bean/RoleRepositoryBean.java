package com.benayn.constell.services.capricorn.repository.bean;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.RoleRepository;
import com.benayn.constell.services.capricorn.repository.domain.AccountRole;
import com.benayn.constell.services.capricorn.repository.domain.AccountRoleExample;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample;
import com.benayn.constell.services.capricorn.repository.mapper.AccountRoleMapper;
import com.benayn.constell.services.capricorn.repository.mapper.RoleMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "roles")
public class RoleRepositoryBean extends GenericRepository<Role, RoleExample, RoleMapper> implements RoleRepository {

    @Override
    @Cacheable(sync = true)
    public List<Role> getByAccountId(Long accountId) {
        AccountRoleMapper accountRoleMapper = getMapper(AccountRoleMapper.class);

        AccountRoleExample accountRoleExample = new AccountRoleExample();
        accountRoleExample.createCriteria().andAccountIdEqualTo(accountId);
        List<AccountRole> accountRoles = accountRoleMapper.selectByExample(accountRoleExample);

        List<Long> roleIds = ofNullable(accountRoles).orElse(newArrayList())
            .stream()
            .map(AccountRole::getRoleId)
            .collect(Collectors.toList());
            ;

        if (roleIds.isEmpty()) {
            return EMPTY_ITEMS;
        }

        RoleExample example = new RoleExample();
        example.createCriteria().andIdIn(roleIds);

        return selectBy(example);
    }

    @Override
    @Cacheable(sync = true)
    public Role getByCode(String code) {
        RoleExample example = new RoleExample();
        example.createCriteria().andCodeEqualTo(code);

        return selectOne(example);
    }
}
