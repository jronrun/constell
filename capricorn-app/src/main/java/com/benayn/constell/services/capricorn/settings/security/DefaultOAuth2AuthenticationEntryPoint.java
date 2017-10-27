package com.benayn.constell.services.capricorn.settings.security;

import static com.benayn.constell.service.util.LZString.encodes;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;

@Slf4j
public class DefaultOAuth2AuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        Cookie cookie = new Cookie("connect.redirect", encodes(ImmutableMap.of(
            "reference", request.getRequestURI(),
            "message", authException.getMessage()
        )));
        cookie.setMaxAge(60);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.sendRedirect("/user/login");
    }

}
