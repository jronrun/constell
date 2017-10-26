package com.benayn.constell.service.server.filter;

import static com.benayn.constell.service.util.LZString.decodes;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Optional.ofNullable;

import com.google.common.base.CharMatcher;
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CookieCredentialsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);

        ofNullable(request.getCookies()).ifPresent(cookies -> {
            Arrays.stream(cookies)
                .filter(cookie -> "connect.credentials".equals(cookie.getName()))
                .findFirst()
                .ifPresent(cookie -> {
                    String value = CharMatcher.is('"').trimFrom(nullToEmpty(decodes(cookie.getValue())));
                    if (!isNullOrEmpty(value)) {
                        mutableRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + value);

                        /* delete cookie
                            cookie.setValue(null);
                            cookie.setMaxAge(0);
                            cookie.setPath("/");
                            response.addCookie(cookie);
                         */
                    }
                });
        });

        chain.doFilter(mutableRequest, res);
    }

    @Override
    public void destroy() {

    }
}
