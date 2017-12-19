package com.benayn.constell.services.capricorn.repository.model;

import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.Setter;

public class RoleDetails extends Role {

    public static final RoleDetails EMPTY = new RoleDetails(new Role(), Lists.newArrayList());

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

    public boolean has(String permission, Predicate<CheckPermission> predicate) {
        return permissions.stream().anyMatch(authority ->
            null == predicate
                ? permission.equals(authority.getCode())
                : predicate.test(CheckPermission.of(permission, authority)));
    }

}
