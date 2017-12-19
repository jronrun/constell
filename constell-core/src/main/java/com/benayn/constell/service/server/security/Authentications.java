package com.benayn.constell.service.server.security;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class Authentications {

    public static ConstellationUserDetails getUserDetails(Authentication authentication) {
        return (ConstellationUserDetails) authentication.getPrincipal();
    }

    public static Long getUserId(Authentication authentication) {
        return getUserDetails(authentication).getId();
    }

    public static List<? extends GrantedAuthority> getAuthorities(Authentication authentication) {
        return Lists.newArrayList(getUserDetails(authentication).getAuthorities());
    }

    public static List<String> getAuthorityNames(Authentication authentication) {
        return getAuthorities(authentication).stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList())
            ;
    }

}
