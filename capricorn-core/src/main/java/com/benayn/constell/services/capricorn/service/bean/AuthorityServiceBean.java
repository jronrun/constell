package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.services.capricorn.repository.PermissionRepository;
import com.benayn.constell.services.capricorn.repository.RoleRepository;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.model.RoleDetails;
import com.benayn.constell.services.capricorn.service.AuthorityService;
import java.util.List;
import java.util.stream.Collectors;
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
        return authorities.stream()
            .map(this::getRoleDetailsByCode)
            .collect(Collectors.toList())
            .stream()
            .anyMatch(role -> role.has(permission));
    }

    @Override
    public List<Role> getRolesByAccountId(Long accountId) {
        return roleRepository.getByAccountId(accountId);
    }

    @Override
    public RoleDetails getRoleDetailsByCode(String roleCode) {
        return getRoleDetailsBy(roleCode, true);
    }

    @Override
    public RoleDetails getRoleDetailsById(Long roleId) {
        return getRoleDetailsBy(roleId, false);
    }

    private <T> RoleDetails getRoleDetailsBy(T param, boolean isFindByCode) {
        Role role = isFindByCode
            ? roleRepository.getByCode((String) param)
            : roleRepository.selectById((Long) param);;
        if (null == role) {
            return RoleDetails.EMPTY;
        }

        List<Permission> permissions = permissionRepository.getByRoleId(role.getId());
        return new RoleDetails(role, permissions);
    }
}
