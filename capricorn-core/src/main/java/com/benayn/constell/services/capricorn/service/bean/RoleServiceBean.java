package com.benayn.constell.services.capricorn.service.bean;

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
import org.springframework.http.HttpStatus;
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
        int result = roleRepository.deleteById(entityId);
        if (result < 1) {
            throw new ServiceException(HttpStatus.NO_CONTENT.value(), "{render.record.none.exist}");
        }

        return result;
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
            if (null != savedRole) {
                throw new ServiceException("{render.record.already.exist}", new Object[] {item.getCode()});
            }

            item.setCreateTime(now);
            result = roleRepository.insertAll(item);
        }
        // update
        else {
            if (null != savedRole && savedRole.getId().longValue() != item.getId()) {
                throw new ServiceException("{render.record.already.exist}", new Object[] {item.getCode()});
            }

            result = roleRepository.updateById(item);
        }

        if (result < 1) {
            throw new ServiceException("{render.record.save.fail}");
        }

        return result;
    }
}
