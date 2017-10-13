package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.service.server.menu.AuthorityMenuBread;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.model.RoleDetails;
import java.util.List;

public interface AuthorityService {

    boolean authenticate(String permission, List<String> authorities);

    List<Role> getRolesByAccountId(Long accountId);

    List<Permission> getPermissionByAccountId(Long accountId);

    RoleDetails getRoleDetailsById(Long roleId);

    RoleDetails getRoleDetailsByCode(String roleCode);

    List<AuthorityMenuBread> getAuthorityMenus();

    List<AuthorityMenuBread> initializeAuthorityMenus(List<AuthorityMenuBread> authorityMenus);
}
