package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.services.capricorn.repository.PermissionRepository;
import com.benayn.constell.services.capricorn.repository.RoleRepository;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.service.AuthorityService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceBean implements AuthorityService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public boolean authenticate(String permission, List<String> authorities) {
        //TODO
        return false;
    }

    @Override
    public List<Role> getRolesByAccountId(Long accountId) {
        return roleRepository.getByAccountId(accountId);
    }
}
