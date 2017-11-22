package com.benayn.constell.services.capricorn.repository.bean;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.enums.CacheName;
import com.benayn.constell.services.capricorn.repository.PermissionRepository;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.PermissionExample;
import com.benayn.constell.services.capricorn.repository.domain.RolePermission;
import com.benayn.constell.services.capricorn.repository.domain.RolePermissionExample;
import com.benayn.constell.services.capricorn.repository.domain.RolePermissionExample.Criteria;
import com.benayn.constell.services.capricorn.repository.mapper.PermissionMapper;
import com.benayn.constell.services.capricorn.repository.mapper.RolePermissionMapper;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = CacheName.PERMISSIONS)
public class PermissionRepositoryBean
    extends GenericRepository<Permission, PermissionExample, PermissionMapper> implements PermissionRepository {

    private RolePermissionMapper rolePermissionMapper;

    @Override
    @Cacheable(value = CacheName.ROLE_PERMISSIONS, sync = true)
    public List<Permission> getByRoleId(Long roleId) {
        List<Long> permissionIds = getRoleOwnerIdsBy(roleId, null, null, null);
        PermissionExample example = new PermissionExample();
        example.createCriteria().andIdIn(permissionIds);

        return selectBy(example);
    }

    @Override
    @Cacheable(sync = true)
    public Permission getByCode(String code) {
        PermissionExample example = new PermissionExample();
        example.createCriteria().andCodeEqualTo(code);

        return selectOne(example);
    }

    @Override
    public List<Long> getRoleOwnerIdsBy(Long roleId, List<Long> permissionIds, Integer pageNo, Integer pageSize) {
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

    @Override
    public int saveRolePermission(List<Long> roleIds, List<Long> permissionIds) {
        final int[] result = {0};

        Date now = new Date();
        roleIds.forEach(roleId -> permissionIds.forEach(permissionId -> {
            RolePermissionExample example = new RolePermissionExample();
            example.createCriteria()
                .andRoleIdEqualTo(roleId)
                .andPermissionIdEqualTo(permissionId)
                ;

            if (rolePermissionMapper.countByExample(example) < 1) {
                RolePermission item = new RolePermission();
                item.setRoleId(roleId);
                item.setPermissionId(permissionId);
                item.setCreateTime(now);
                item.setLastModifyTime(now);
                result[0] += rolePermissionMapper.insert(item);
            }

        }));

        return result[0];
    }

    @Override
    public int deleteRolePermission(List<Long> roleIds, List<Long> permissionIds) {
        final int[] result = {0};

        roleIds.forEach(roleId -> {
            RolePermissionExample example = new RolePermissionExample();
            example.createCriteria()
                .andRoleIdEqualTo(roleId)
                .andPermissionIdIn(permissionIds)
            ;

            result[0] += rolePermissionMapper.deleteByExample(example);
        });

        return result[0];
    }

    @Override
    protected void setup() {
        this.rolePermissionMapper = getMapper(RolePermissionMapper.class);
    }
}
