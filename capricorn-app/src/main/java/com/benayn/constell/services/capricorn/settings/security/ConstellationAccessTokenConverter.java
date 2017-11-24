package com.benayn.constell.services.capricorn.settings.security;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

public class ConstellationAccessTokenConverter extends DefaultAccessTokenConverter {

    public ConstellationAccessTokenConverter(UserDetailsService userDetailsService) {
        setUserTokenConverter(new ConstellationUserAuthenticationConverter(userDetailsService));
    }

    public class ConstellationUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

        ConstellationUserAuthenticationConverter(UserDetailsService userDetailsService) {
            setUserDetailsService(userDetailsService);
        }

        @Override
        public Authentication extractAuthentication(Map<String, ?> map) {
            return super.extractAuthentication(map);
        }
    }

}
