package com.benayn.constell.services.capricorn.settings.security;


import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.repository.model.AccountDetails;
import com.benayn.constell.services.capricorn.service.AccountService;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ConstellationUserDetailsService implements UserDetailsService {

    /**
     *
     */
    private final AccountService userService;

    @Autowired
    public ConstellationUserDetailsService(AccountService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountDetails account = userService.getAccountDetails(username);

        if (account == null) {
            throw new UsernameNotFoundException("User " + username + " not found.");
        }

        if (account.getRoles() == null || account.getRoles().isEmpty()) {
            throw new UsernameNotFoundException("User not authorized.");
        }

        //Wraps a role to SimpleGrantedAuthority objects
        Collection<GrantedAuthority> grantedAuthorities = account.getRoles()
            .stream()
            .map(role -> new SimpleGrantedAuthority(role.getCode()))
            .collect(Collectors.toList())
            ;

        return new User(account.getEmail(),
            account.getPassword(), account.isEnabled(),
            !account.isExpired(), !account.isCredentialsExpired(),
            !account.isLocked(), grantedAuthorities);
    }

}

