package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.service.server.menu.AuthorityMenuGroup;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.model.CheckPermission;
import com.benayn.constell.services.capricorn.repository.model.RoleDetails;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface AuthorityService {

    boolean authenticate(String permission, List<String> authorities, Predicate<CheckPermission> predicate);

    List<Role> getRolesByAccountId(Long accountId);

    List<Permission> getPermissionByAccountId(Long accountId);

    RoleDetails getRoleDetailsById(Long roleId);

    RoleDetails getRoleDetailsByCode(String roleCode);

    List<AuthorityMenuGroup> getMenuGroup();

    List<AuthorityMenuGroup> initializeMenuGroup(List<AuthorityMenuGroup> authorityMenus);
}
