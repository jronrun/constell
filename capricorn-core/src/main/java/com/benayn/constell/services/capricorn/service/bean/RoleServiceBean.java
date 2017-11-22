package com.benayn.constell.services.capricorn.service.bean;

import static com.benayn.constell.service.util.Assets.checkNotNull;
import static com.benayn.constell.service.util.Assets.checkRecordDeleted;
import static com.benayn.constell.service.util.Assets.checkRecordNoneExist;
import static com.benayn.constell.service.util.Assets.checkRecordSaved;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.TouchRelation;
import com.benayn.constell.services.capricorn.enums.CacheName;
import com.benayn.constell.services.capricorn.repository.RoleRepository;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample.Criteria;
import com.benayn.constell.services.capricorn.service.RoleService;
import com.benayn.constell.services.capricorn.viewobject.RoleVo;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceBean implements RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceBean(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role selectById(long entityId) {
        return roleRepository.selectById(entityId);
    }

    @Override
    public Page<Role> selectPageBy(RoleVo condition) {
        RoleExample example = new RoleExample();
        Criteria criteria = example.createCriteria();

        if (null != condition.getCode()) {
            criteria.andCodeLike(condition.like(condition.getCode()));
        }

        if (null != condition.getLabel()) {
            criteria.andLabelLike(condition.like(condition.getLabel()));
        }

        if (condition.hasTouchOwner()) {
            List<Long> itemIds = Lists.newArrayList();
            switch (condition.getTouchModule()) {
                case "account":
                    itemIds = roleRepository
                        .getAccountOwnerIdsBy(condition.getTouchId(), null, condition.getPageNo(), condition.getPageSize());
                    break;
                case "permission":
                    itemIds = roleRepository
                        .getPermissionOwnerIdsBy(condition.getTouchId(), null, condition.getPageNo(), condition.getPageSize());
                    break;
            }

            if (itemIds.size() < 1) {
                itemIds.add(-1L);
            }

            criteria.andIdIn(itemIds);
        }

        Page<Role> page = roleRepository.selectPageBy(example, condition.getPageNo(), condition.getPageSize());

        if (condition.hasTouch()) {
            List<Long> checkItemIds = page.getResource().stream()
                .map(Role::getId)
                .collect(Collectors.toList())
                ;

            List<Long> ownerIds = Lists.newArrayList();

            switch (condition.getTouchModule()) {
                case "account":
                    ownerIds = roleRepository
                        .getAccountOwnerIdsBy(condition.getTouchId(), checkItemIds, null, null);
                    break;
                case "permission":
                    ownerIds = roleRepository
                        .getPermissionOwnerIdsBy(condition.getTouchId(), checkItemIds, null, null);
                    break;
            }

            page.setAsTouchOwnerIds(ownerIds);
        }

        return page;
    }

    @Override
    @CacheEvict(value = {
        CacheName.ROLES,
        CacheName.MENUS,
        CacheName.ACCOUNT_ROLES
    }, allEntries = true)
    public int deleteById(Long entityId) throws ServiceException {
        return checkRecordDeleted(roleRepository.deleteById(entityId));
    }

    @Override
    @CacheEvict(cacheNames = {CacheName.ACCOUNT_ROLES, CacheName.MENUS}, allEntries = true)
    public int createAccountRole(TouchRelation relation) throws ServiceException {
        return roleRepository.saveAccountRole(
            checkNotNull(relation).getSlaveNumberIds(), relation.getMasterNumberIds());
    }

    @Override
    @CacheEvict(cacheNames = {CacheName.ACCOUNT_ROLES, CacheName.MENUS}, allEntries = true)
    public int deleteAccountRole(TouchRelation relation) throws ServiceException {
        return roleRepository.deleteAccountRole(
            checkNotNull(relation).getSlaveNumberIds(), relation.getMasterNumberIds());
    }

    @Override
    @CacheEvict(value = {
        CacheName.ROLES,
        CacheName.MENUS,
        CacheName.ACCOUNT_ROLES
    }, condition = "null != #entity && null != #entity.id", allEntries = true)
    public int save(RoleVo entity) throws ServiceException {
        Date now = new Date();

        Role item = new Role();
        item.setId(entity.getId());
        item.setCode(entity.getCode());
        item.setLabel(entity.getLabel());
        item.setLastModifyTime(now);

        int result;
        Role savedRole = roleRepository.getByCode(item.getCode());

        // create
        if (null == item.getId()) {
            checkRecordNoneExist(null == savedRole, item.getLabel());

            item.setCreateTime(now);
            result = roleRepository.insertAll(item);
        }
        // update
        else {
            checkRecordNoneExist(null == savedRole
                || savedRole.getId().longValue() == item.getId(), item.getLabel());
            result = roleRepository.updateById(item);
        }

        return checkRecordSaved(result);
    }
}
