package com.benayn.constell.services.capricorn.repository.bean;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.RoleRepository;
import com.benayn.constell.services.capricorn.repository.domain.AccountRole;
import com.benayn.constell.services.capricorn.repository.domain.AccountRoleExample;
import com.benayn.constell.services.capricorn.repository.domain.AccountRoleExample.Criteria;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample;
import com.benayn.constell.services.capricorn.repository.domain.RolePermission;
import com.benayn.constell.services.capricorn.repository.domain.RolePermissionExample;
import com.benayn.constell.services.capricorn.repository.mapper.AccountRoleMapper;
import com.benayn.constell.services.capricorn.repository.mapper.RoleMapper;
import com.benayn.constell.services.capricorn.repository.mapper.RolePermissionMapper;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "roles")
public class RoleRepositoryBean extends GenericRepository<Role, RoleExample, RoleMapper> implements RoleRepository {

    private AccountRoleMapper accountRoleMapper;
    private RolePermissionMapper rolePermissionMapper;

    @Override
    @Cacheable(sync = true)
    public List<Role> getByAccountId(Long accountId) {
        List<Long> roleIds = getAccountOwnerIdsBy(accountId, null, null, null);

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

    @Override
    protected void setup() {
        this.accountRoleMapper = getMapper(AccountRoleMapper.class);
        this.rolePermissionMapper = getMapper(RolePermissionMapper.class);
    }

    @Override
    public int saveAccountRole(List<Long> accountIds, List<Long> roleIds) {
        final int[] result = {0};

        Date now = new Date();
        accountIds.forEach(accountId -> roleIds.forEach(roleId -> {
            AccountRoleExample example = new AccountRoleExample();
            example.createCriteria()
                .andAccountIdEqualTo(accountId)
                .andRoleIdEqualTo(roleId)
            ;

            if (accountRoleMapper.countByExample(example) < 1) {
                AccountRole item = new AccountRole();
                item.setAccountId(accountId);
                item.setRoleId(roleId);
                item.setCreateTime(now);
                item.setLastModifyTime(now);
                result[0] += accountRoleMapper.insert(item);
            }

        }));

        return result[0];
    }

    @Override
    public int deleteAccountRole(List<Long> accountIds, List<Long> roleIds) {
        final int[] result = {0};

        accountIds.forEach(accountId -> {
            AccountRoleExample example = new AccountRoleExample();
            example.createCriteria()
                .andRoleIdEqualTo(accountId)
                .andRoleIdIn(roleIds)
            ;

            result[0] += accountRoleMapper.deleteByExample(example);
        });

        return result[0];
    }

    @Override
    public List<Long> getAccountOwnerIdsBy(Long accountId, List<Long> roleIds, Integer pageNo, Integer pageSize) {
        AccountRoleExample accountRoleExample = new AccountRoleExample();
        Criteria criteria = accountRoleExample.createCriteria().andAccountIdEqualTo(accountId);

        if (null != roleIds && roleIds.size() > 0) {
            criteria.andRoleIdIn(roleIds);
        }

        if (null != pageNo && null != pageSize) {
            addPageFeature(accountRoleExample, pageNo, pageSize);
        }

        List<AccountRole> rolePermissions = accountRoleMapper.selectByExample(accountRoleExample);

        return ofNullable(rolePermissions).orElse(newArrayList())
            .stream()
            .map(AccountRole::getRoleId)
            .collect(Collectors.toList())
            ;
    }

    @Override
    public List<Long> getPermissionOwnerIdsBy(Long permissionId, List<Long> roleIds, Integer pageNo, Integer pageSize) {
        RolePermissionExample rolePermissionExample = new RolePermissionExample();
        RolePermissionExample.Criteria criteria =
            rolePermissionExample.createCriteria().andPermissionIdEqualTo(permissionId);

        if (null != roleIds && roleIds.size() > 0) {
            criteria.andRoleIdIn(roleIds);
        }

        if (null != pageNo && null != pageSize) {
            addPageFeature(rolePermissionExample, pageNo, pageSize);
        }

        List<RolePermission> rolePermissions = rolePermissionMapper.selectByExample(rolePermissionExample);

        return ofNullable(rolePermissions).orElse(newArrayList())
            .stream()
            .map(RolePermission::getRoleId)
            .collect(Collectors.toList())
            ;
    }
}
