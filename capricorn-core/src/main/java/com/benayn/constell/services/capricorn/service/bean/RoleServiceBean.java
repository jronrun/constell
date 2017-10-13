package com.benayn.constell.services.capricorn.service.bean;

import static com.benayn.constell.service.util.Assets.checkRecordDeleted;
import static com.benayn.constell.service.util.Assets.checkRecordNoneExist;
import static com.benayn.constell.service.util.Assets.checkRecordSaved;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.services.capricorn.repository.RoleRepository;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample.Criteria;
import com.benayn.constell.services.capricorn.service.RoleService;
import com.benayn.constell.services.capricorn.viewobject.RoleVo;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
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

        return roleRepository.selectPageBy(example, condition.getPageNo(), condition.getPageSize());
    }

    @Override
    public int deleteById(Long entityId) throws ServiceException {
        return checkRecordDeleted(roleRepository.deleteById(entityId));
    }

    @Override
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
            checkRecordNoneExist(null == savedRole, item.getCode());

            item.setCreateTime(now);
            result = roleRepository.insertAll(item);
        }
        // update
        else {
            checkRecordNoneExist(null == savedRole
                || savedRole.getId().longValue() == item.getId(), item.getCode());
            result = roleRepository.updateById(item);
        }

        return checkRecordSaved(result);
    }
}
