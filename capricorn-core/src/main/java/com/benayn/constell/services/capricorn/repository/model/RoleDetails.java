package com.benayn.constell.services.capricorn.repository.model;

import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class RoleDetails extends Role {

    public RoleDetails(Role role, List<Permission> permissions) {
        setId(role.getId());
        setCode(role.getCode());
        setLabel(role.getLabel());
        setCreateTime(role.getCreateTime());
        setLastModifyTime(role.getLastModifyTime());
        setPermissions(permissions);
    }

    @Getter
    @Setter
    private List<Permission> permissions;

    public boolean has(String permission) {
        return permissions.stream().anyMatch(authority -> permission.equals(authority.getCode()));
    }

}
