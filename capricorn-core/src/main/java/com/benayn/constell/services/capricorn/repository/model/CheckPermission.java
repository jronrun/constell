package com.benayn.constell.services.capricorn.repository.model;

import com.benayn.constell.services.capricorn.repository.domain.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class CheckPermission {

    private String permissionName;
    private Permission permission;

}
