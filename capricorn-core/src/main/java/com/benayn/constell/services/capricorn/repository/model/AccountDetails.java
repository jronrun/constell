package com.benayn.constell.services.capricorn.repository.model;

import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class AccountDetails extends Account {

    public AccountDetails(Account account, List<Role> roles) {
        setId(account.getId());
        setUsername(account.getUsername());
        setEmail(account.getEmail());
        setPassword(account.getPassword());
        setGender(account.getGender());
        setStatus(account.getStatus());
        setCreateTime(account.getCreateTime());
        setLastModifyTime(account.getLastModifyTime());
        setEnabled(account.isEnabled());
        setCredentialsExpired(account.isCredentialsExpired());
        setExpired(account.isExpired());
        setLocked(account.isLocked());

        setRoles(roles);
    }

    @Getter
    @Setter
    private List<Role> roles;

}
