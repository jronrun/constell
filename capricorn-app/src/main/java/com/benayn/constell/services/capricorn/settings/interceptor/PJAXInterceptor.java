package com.benayn.constell.services.capricorn.settings.interceptor;

import static com.google.common.base.Strings.isNullOrEmpty;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class PJAXInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        @Nullable ModelAndView modelAndView) throws Exception {

        if (null != modelAndView) {
            String headerPJAX = request.getHeader("X-PJAX");
            boolean isPJAX = !isNullOrEmpty(headerPJAX) && "true".equalsIgnoreCase(headerPJAX);

            modelAndView.getModel().put("isPJAX", isPJAX);
        }

        super.postHandle(request, response, handler, modelAndView);
    }
}
