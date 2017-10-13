package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.repository.PermissionRepository;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.PermissionExample;
import com.benayn.constell.services.capricorn.repository.domain.PermissionExample.Criteria;
import com.benayn.constell.services.capricorn.service.PermissionService;
import com.benayn.constell.services.capricorn.viewobject.PermissionVo;
import com.google.common.base.Ascii;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public boolean saveFromAuthorities() {
        Arrays
            .stream(Authorities.class.getDeclaredFields())
            .forEach(field -> {
                String code = field.getName();
                if (null == permissionRepository.getByCode(code)) {
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

    @Override
    public int deleteById(Long entityId) throws ServiceException {
        int result = permissionRepository.deleteById(entityId);
        if (result < 1) {
            throw new ServiceException(HttpStatus.NO_CONTENT.value(), "{render.record.none.exist}");
        }

        return result;
    }
}