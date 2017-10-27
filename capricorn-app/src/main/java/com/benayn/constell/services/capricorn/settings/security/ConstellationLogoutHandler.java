package com.benayn.constell.services.capricorn.settings.security;

import static com.benayn.constell.service.util.LZString.decodes;
import static java.util.Optional.ofNullable;

import com.alibaba.fastjson.TypeReference;
import com.benayn.constell.service.common.Pair;
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
                Pair<String, String> token =
                    decodes(cookie.getValue(), new TypeReference<Pair<String, String>>(){});

                ofNullable(token).ifPresent(tkn -> {
                    consumerTokenServices.revokeToken(tkn.getKey());
                });

                /* delete cookie
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                 */
            }));

    }
}
