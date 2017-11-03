package com.benayn.constell.services.capricorn.repository.bean;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.PermissionRepository;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.PermissionExample;
import com.benayn.constell.services.capricorn.repository.domain.RolePermission;
import com.benayn.constell.services.capricorn.repository.domain.RolePermissionExample;
import com.benayn.constell.services.capricorn.repository.domain.RolePermissionExample.Criteria;
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
        List<Long> permissionIds = getOwnerIdsBy(roleId, null, null, null);
        PermissionExample example = new PermissionExample();
        example.createCriteria().andIdIn(permissionIds);

        return selectBy(example);
    }

    @Override
    public Permission getByCode(String code) {
        PermissionExample example = new PermissionExample();
        example.createCriteria().andCodeEqualTo(code);

        return selectOne(example);
    }

    @Override
    public List<Long> getOwnerIdsBy(Long roleId, List<Long> permissionIds, Integer pageNo, Integer pageSize) {
        RolePermissionMapper rolePermissionMapper = getMapper(RolePermissionMapper.class);

        RolePermissionExample rolePermissionExample = new RolePermissionExample();
        Criteria criteria = rolePermissionExample.createCriteria().andRoleIdEqualTo(roleId);

        if (null != permissionIds && permissionIds.size() > 0) {
            criteria.andPermissionIdIn(permissionIds);
        }

        if (null != pageNo && null != pageSize) {
            addPageFeature(rolePermissionExample, pageNo, pageSize);
        }

        List<RolePermission> rolePermissions = rolePermissionMapper.selectByExample(rolePermissionExample);

        return ofNullable(rolePermissions).orElse(newArrayList())
            .stream()
            .map(RolePermission::getPermissionId)
            .collect(Collectors.toList())
        ;
    }
}
