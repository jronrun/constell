package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.services.capricorn.condition.RoleCondition;
import com.benayn.constell.services.capricorn.repository.RoleRepository;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample.Criteria;
import com.benayn.constell.services.capricorn.service.RoleService;
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
    public Page<Role> selectPageBy(RoleCondition condition) {
        RoleExample example = new RoleExample();
        Criteria criteria = example.createCriteria();

        if (null != condition.getCode()) {
            if (condition.isLike("code")) {
                criteria.andCodeLike(condition.getCode());
            } else {
                criteria.andCodeEqualTo(condition.getCode());
            }
        }

        if (null != condition.getLabel()) {
            if (condition.isLike("label")) {
                criteria.andLabelLike(condition.getLabel());
            } else {
                criteria.andLabelEqualTo(condition.getLabel());
            }
        }

        return roleRepository.selectPageBy(example, condition.getPageNo(), condition.getPageSize());
    }
}
