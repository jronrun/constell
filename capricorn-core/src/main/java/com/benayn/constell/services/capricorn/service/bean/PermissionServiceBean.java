package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.services.capricorn.repository.PermissionRepository;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.PermissionExample;
import com.benayn.constell.services.capricorn.repository.domain.PermissionExample.Criteria;
import com.benayn.constell.services.capricorn.service.PermissionService;
import com.benayn.constell.services.capricorn.viewobject.PermissionVo;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Page<Permission> selectPageBy(PermissionVo condition) {
        PermissionExample example = new PermissionExample();
        Criteria criteria = example.createCriteria();

        if (null != condition.getCode()) {
            if (condition.isLike("code")) {
                criteria.andCodeLike(condition.like(condition.getCode()));
            } else {
                criteria.andCodeEqualTo(condition.getCode());
            }
        }

        if (null != condition.getLabel()) {
            if (condition.isLike("label")) {
                criteria.andLabelLike(condition.like(condition.getLabel()));
            } else {
                criteria.andLabelEqualTo(condition.getLabel());
            }
        }

        return permissionRepository.selectPageBy(example, condition.getPageNo(), condition.getPageSize());
    }

    @Override
    public int save(PermissionVo entity) throws ServiceException {
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
            if (null != savedPermission) {
                throw new ServiceException("{render.record.already.exist}", new Object[] {item.getCode()});
            }

            item.setCreateTime(now);
            result = permissionRepository.insertAll(item);
        }
        // update
        else {
            if (null != savedPermission && savedPermission.getId().longValue() != item.getId()) {
                throw new ServiceException("{render.record.already.exist}", new Object[] {item.getCode()});
            }

            result = permissionRepository.updateById(item);
        }

        if (result < 1) {
            throw new ServiceException("{render.record.save.fail}");
        }

        return result;
    }
}
