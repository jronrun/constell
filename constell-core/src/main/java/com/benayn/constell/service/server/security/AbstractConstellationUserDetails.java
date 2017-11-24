package com.benayn.constell.service.server.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class AbstractConstellationUserDetails implements ConstellationUserDetails {

    @Getter
    private UserDetails delegate;

    public AbstractConstellationUserDetails(String username, String password, boolean enabled,
        boolean accountNonExpired, boolean credentialsNonExpired,
        boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        this.delegate = new User(username, password, enabled,
            accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getDelegate().getAuthorities();
    }

    @Override
    public String getPassword() {
        return getDelegate().getPassword();
    }

    @Override
    public String getUsername() {
        return getDelegate().getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return getDelegate().isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return getDelegate().isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return getDelegate().isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return getDelegate().isEnabled();
    }

    @Override
    public String getName() {
        return getNickname();
    }
}
