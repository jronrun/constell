package com.benayn.constell.service.server.security;

import com.benayn.constell.service.enums.Gender;
import java.util.Date;
import org.springframework.security.core.userdetails.UserDetails;

public interface ConstellationUserDetails extends UserDetails {

    Long getId();

    String getNickname();

    Gender getGender();

    Date getCreateTime();

}
