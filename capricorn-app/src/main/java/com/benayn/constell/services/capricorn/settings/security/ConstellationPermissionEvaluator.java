package com.benayn.constell.services.capricorn.settings.security;

import com.benayn.constell.services.capricorn.service.AuthorityService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class ConstellationPermissionEvaluator implements PermissionEvaluator {

    private AuthorityService authorityService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (!authentication.isAuthenticated()) {
            return false;
        }

        if (!(permission instanceof String)) {
            return false;
        }

        Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
        if (grantedAuthorities.isEmpty()) {
            return false;
        }

        List<String> authorities = grantedAuthorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList())
            ;

        return authorityService.authenticate((String) permission, authorities, null);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

    @Autowired
    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }
}
