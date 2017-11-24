package com.benayn.constell.services.capricorn.settings.security;


import com.benayn.constell.services.capricorn.enums.AccountStatus;
import com.benayn.constell.services.capricorn.repository.model.AccountDetails;
import com.benayn.constell.services.capricorn.service.AccountService;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

        if (AccountStatus.DELETED.getValue().shortValue() == account.getStatus()) {
            throw new UsernameNotFoundException("User " + username + " deleted.");
        }

        if (account.getRoles() == null || account.getRoles().isEmpty()) {
            throw new UsernameNotFoundException("User not authorized.");
        }

        Collection<GrantedAuthority> grantedAuthorities = account.getRoles()
            .stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getCode()))
            .collect(Collectors.toList())
            ;

        return new ConstellationUser(account.getEmail(),
            account.getPassword(), account.isEnabled(),
            !account.isExpired(), !account.isCredentialsExpired(),
            !account.isLocked(), grantedAuthorities, account);
    }

}

