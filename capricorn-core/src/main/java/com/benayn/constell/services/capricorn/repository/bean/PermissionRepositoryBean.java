package com.benayn.constell.services.capricorn.repository.bean;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.PermissionRepository;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.PermissionExample;
import com.benayn.constell.services.capricorn.repository.domain.RolePermission;
import com.benayn.constell.services.capricorn.repository.domain.RolePermissionExample;
import com.benayn.constell.services.capricorn.repository.mapper.PermissionMapper;
import com.benayn.constell.services.capricorn.repository.mapper.RolePermissionMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "permissions")
public class PermissionRepositoryBean
    extends GenericRepository<Permission, PermissionExample, PermissionMapper> implements PermissionRepository {

    @Override
    @Cacheable(sync = true)
    public List<Permission> getByRoleId(Long roleId) {
        RolePermissionMapper rolePermissionMapper = getMapper(RolePermissionMapper.class);

        RolePermissionExample rolePermissionExample = new RolePermissionExample();
        rolePermissionExample.createCriteria().andRoleIdEqualTo(roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectByExample(rolePermissionExample);

        List<Long> permissionIds = ofNullable(rolePermissions).orElse(newArrayList())
            .stream()
            .map(RolePermission::getPermissionId)
            .collect(Collectors.toList());
            ;

        if (permissionIds.isEmpty()) {
            return EMPTY_ITEMS;
        }

        PermissionExample example = new PermissionExample();
        example.createCriteria().andIdIn(permissionIds);

        return selectBy(example);
    }
}
