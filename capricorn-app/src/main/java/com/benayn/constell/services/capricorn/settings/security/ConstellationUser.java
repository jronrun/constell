package com.benayn.constell.services.capricorn.settings.security;

import com.benayn.constell.service.enums.Gender;
import com.benayn.constell.service.server.security.AbstractConstellationUserDetails;
import com.benayn.constell.services.capricorn.enums.AccountStatus;
import com.benayn.constell.services.capricorn.repository.model.AccountDetails;
import java.util.Collection;
import java.util.Date;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class ConstellationUser extends AbstractConstellationUserDetails {

    private Long id;
    private String nickname;
    private Gender gender;
    private AccountStatus status;
    private Date createTime;

    ConstellationUser(String username, String password, boolean enabled, boolean accountNonExpired,
        boolean credentialsNonExpired,
        boolean accountNonLocked,
        Collection<? extends GrantedAuthority> authorities, AccountDetails account) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.id = account.getId();
        this.nickname = account.getUsername();
        this.gender = Gender.get(account.getGender());
        this.status = AccountStatus.get(account.getStatus());
        this.createTime = account.getCreateTime();
    }

}
