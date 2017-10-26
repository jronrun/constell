package com.benayn.constell.service.server.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

final class MutableHttpServletRequest extends HttpServletRequestWrapper {

    // holds custom header and value mapping
    private final Map<String, String> customHeaders;

    MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.customHeaders = Maps.newHashMap();
    }

    public void addHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        // check the custom headers first
        String headerValue = customHeaders.get(name);

        if (headerValue != null) {
            return headerValue;
        }
        // else return from into the original wrapped object
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> value = Lists.newArrayList();

        Enumeration<String> headers = super.getHeaders(name);
        if (null != headers) {
            while (headers.hasMoreElements()) {
                value.add(headers.nextElement());
            }
        }

        String namedValue = getHeader(name);
        if (null != namedValue) {
            value.add(namedValue);
        }

        return Collections.enumeration(value);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        // create a set of the custom header names
        Set<String> set = Sets.newHashSet(customHeaders.keySet());

        // now add the headers from the wrapped request object
        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            // add the names of the request headers into the list
            String n = e.nextElement();
            set.add(n);
        }

        // create an enumeration from the set and return
        return Collections.enumeration(set);
    }
}
