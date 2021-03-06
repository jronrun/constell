package com.benayn.constell.services.capricorn.service.bean;

import static com.benayn.constell.service.util.Assets.checkNotNull;
import static com.benayn.constell.service.util.Assets.checkRecordDeleted;
import static com.benayn.constell.service.util.Assets.checkRecordNoneExist;
import static com.benayn.constell.service.util.Assets.checkRecordSaved;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.TouchRelation;
import com.benayn.constell.service.util.Likes;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.enums.CacheName;
import com.benayn.constell.services.capricorn.repository.PermissionRepository;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.PermissionExample;
import com.benayn.constell.services.capricorn.repository.domain.PermissionExample.Criteria;
import com.benayn.constell.services.capricorn.service.PermissionService;
import com.benayn.constell.services.capricorn.viewobject.PermissionVO;
import com.google.common.base.Ascii;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceBean implements PermissionService {

    private PermissionRepository permissionRepository;

    @Autowired
    public PermissionServiceBean(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Permission selectById(long entityId) {
        return permissionRepository.selectById(entityId);
    }

    @Override
    public Page<Permission> selectPageBy(PermissionVO condition) {
        PermissionExample example = new PermissionExample();
        Criteria criteria = example.createCriteria();

        if (null != condition.getCode()) {
            criteria.andCodeLike(Likes.get(condition.getCode()));
        }

        if (null != condition.getLabel()) {
            criteria.andLabelLike(Likes.get(condition.getLabel()));
        }

        if (condition.hasTouchOwner()) {
            List<Long> itemIds = permissionRepository
                .getRoleOwnerIdsBy(condition.getTouchId(), null, condition.getPageNo(), condition.getPageSize());
            if (itemIds.size() < 1) {
                itemIds.add(-1L);
            }
            criteria.andIdIn(itemIds);
        }

        Page<Permission> page = permissionRepository.selectPageBy(example, condition.getPageNo(), condition.getPageSize());

        if (condition.hasTouch()) {
            List<Long> checkItemIds = page.getResource().stream()
                .map(Permission::getId)
                .collect(Collectors.toList())
                ;

            List<Long> ownerIds = permissionRepository
                .getRoleOwnerIdsBy(condition.getTouchId(), checkItemIds, null, null);
            page.setAsTouchOwnerIds(ownerIds);
        }

        return page;
    }

    private static final List<String> EXCLUDE_AUTHORITIES = Lists.newArrayList(
        Authorities.ROLE_CAPRICORN
    );

    @Override
    public boolean saveFromAuthorities() {
        Arrays
            .stream(Authorities.class.getDeclaredFields())
            .forEach(field -> {
                String code = field.getName();
                if (!EXCLUDE_AUTHORITIES.contains(code) && null == permissionRepository.getByCode(code)) {
                    Date now = new Date();
                    Permission permission = new Permission();
                    permission.setCode(code);
                    permission.setLabel(asLabel(code));
                    permission.setCreateTime(now);
                    permission.setLastModifyTime(now);

                    permissionRepository.insertAll(permission);
                }
            });

        return true;
    }

    private String asLabel(String code) {
        return Joiner.on(" ").join(
            Splitter.on("_").splitToList(code).stream()
            .map(label -> Ascii.toUpperCase(label.charAt(0)) + Ascii.toLowerCase(label.substring(1)))
            .collect(Collectors.toList()));
    }

    @Override
    @CacheEvict(cacheNames = {CacheName.MENUS, CacheName.ACCOUNT_ROLES, CacheName.ROLE_PERMISSIONS}, allEntries = true)
    public int createRolePermission(TouchRelation relation) throws ServiceException {
        return permissionRepository.saveRolePermission(
            checkNotNull(relation).getSlaveNumberIds(), relation.getMasterNumberIds());
    }

    @Override
    @CacheEvict(cacheNames = {CacheName.MENUS, CacheName.ACCOUNT_ROLES, CacheName.ROLE_PERMISSIONS}, allEntries = true)
    public int deleteRolePermission(TouchRelation relation) throws ServiceException {
        return permissionRepository.deleteRolePermission(
            checkNotNull(relation).getSlaveNumberIds(), relation.getMasterNumberIds());
    }

    @Override
    @CacheEvict(value = {
        CacheName.PERMISSIONS,
        CacheName.MENUS,
        CacheName.ACCOUNT_ROLES,
        CacheName.ROLE_PERMISSIONS
    }, condition = "null != #entity && null != #entity.id", allEntries = true)
    public int save(PermissionVO entity) throws ServiceException {
        Date now = new Date();

        Permission item = new Permission();
        item.setId(entity.getId());
        item.setCode(entity.getCode());
        item.setLabel(entity.getLabel());
        item.setLastModifyTime(now);

        int result;
        Permission savedPermission = permissionRepository.getByCode(item.getCode());

        // create
        if (null == item.getId()) {
            checkRecordNoneExist(null == savedPermission, item.getCode());

            item.setCreateTime(now);
            result = permissionRepository.insertAll(item);
        }
        // update
        else {
            checkRecordNoneExist(null == savedPermission
                || savedPermission.getId().longValue() == item.getId(), item.getCode());
            result = permissionRepository.updateById(item);
        }

        return checkRecordSaved(result);
    }

    @Override
    @CacheEvict(value = {
        CacheName.PERMISSIONS,
        CacheName.MENUS,
        CacheName.ACCOUNT_ROLES,
        CacheName.ROLE_PERMISSIONS
    }, allEntries = true)
    public int deleteById(Long entityId) throws ServiceException {
        return checkRecordDeleted(permissionRepository.deleteById(entityId));
    }
}
