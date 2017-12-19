package com.benayn.constell.services.capricorn.service.bean;

import static com.benayn.constell.services.capricorn.config.Authorities.ROLE_CAPRICORN;
import static com.google.common.base.Strings.isNullOrEmpty;

import com.benayn.constell.service.server.menu.AuthorityMenuGroup;
import com.benayn.constell.services.capricorn.repository.PermissionRepository;
import com.benayn.constell.services.capricorn.repository.RoleRepository;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.model.CheckPermission;
import com.benayn.constell.services.capricorn.repository.model.RoleDetails;
import com.benayn.constell.services.capricorn.service.AuthorityService;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceBean implements AuthorityService {

    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;

    @Autowired
    public AuthorityServiceBean(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public boolean authenticate(String permission, List<String> authorities, Predicate<CheckPermission> predicate) {
        if (isNullOrEmpty(permission)) {
            return false;
        }

        if (authorities.stream().anyMatch(ROLE_CAPRICORN::equals)) {
            return true;
        }

        /*
        return authorities.stream() .map(this::getRoleDetailsByCode) .collect(Collectors.toList())
            .stream() .anyMatch(role -> role.has(permission));
         */

        return authorities.stream()
            .anyMatch(authority -> getRoleDetailsByCode(authority).has(permission, predicate));
    }

    @Override
    @Cacheable(value = "_menus", key = "'_menus'", sync = true)
    public List<AuthorityMenuGroup> getMenuGroup() {
        return Lists.newArrayList();
    }

    @Override
    @CachePut(value = "_menus", key = "'_menus'")
    public List<AuthorityMenuGroup> initializeMenuGroup(List<AuthorityMenuGroup> authorityMenus) {
        return authorityMenus;
    }

    @Override
    public List<Role> getRolesByAccountId(Long accountId) {
        return roleRepository.getByAccountId(accountId);
    }

    @Override
    public List<Permission> getPermissionByAccountId(Long accountId) {
        List<Role> roles = getRolesByAccountId(accountId);
        List<Permission> permissions = Lists.newArrayList();

        roles.forEach(role -> {
            RoleDetails roleDetails = getRoleDetailsById(role.getId());
            permissions.addAll(roleDetails.getPermissions());
        });

        return permissions;
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
            : roleRepository.selectById((Long) param);
        if (null == role) {
            return RoleDetails.EMPTY;
        }

        List<Permission> permissions = permissionRepository.getByRoleId(role.getId());
        return new RoleDetails(role, permissions);
    }
}
