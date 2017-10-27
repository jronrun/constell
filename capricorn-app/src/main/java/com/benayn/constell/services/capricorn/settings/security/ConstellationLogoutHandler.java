package com.benayn.constell.services.capricorn.settings.security;

import static com.benayn.constell.service.util.LZString.decodes;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Optional.ofNullable;

import com.google.common.base.CharMatcher;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class ConstellationLogoutHandler implements LogoutHandler {

    private ConsumerTokenServices consumerTokenServices;

    @Autowired
    @Qualifier("consumerTokenServices")
    public void setTokenServices(ConsumerTokenServices consumerTokenServices) {
        this.consumerTokenServices = consumerTokenServices;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        ofNullable(request.getCookies()).ifPresent(cookies -> Arrays.stream(cookies)
            .filter(cookie -> "connect.credentials".equals(cookie.getName()))
            .findFirst()
            .ifPresent(cookie -> {
                String value = CharMatcher.is('"').trimFrom(nullToEmpty(decodes(cookie.getValue())));
                if (!isNullOrEmpty(value)) {
                    consumerTokenServices.revokeToken(value);
                }
            }));

    }
}
