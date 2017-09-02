package com.benayn.constell.services.capricorn.settings.listener;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import com.benayn.constell.service.server.menu.AuthorityMenuitem;
import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.services.capricorn.service.AuthorityService;
import com.benayn.constell.services.capricorn.settings.constant.Authorities;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
@Slf4j
public class ApplicationEventListener {

    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @EventListener
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
        List<AuthorityMenuitem> menus = Lists.newArrayList();
        handlerMapping.getHandlerMethods().forEach((key, value) -> {
            MenuCapability menu = value.getMethodAnnotation(MenuCapability.class);
            if (null == menu) {
                return;
            }

            String action;
            List<String> patterns = Lists.newArrayList(key.getPatternsCondition().getPatterns());
            if (patterns.isEmpty() || (action = patterns.get(0)).contains("{")) {
                log.warn("unsupported menu capability {} : {}", menu.value(), key);
                return;
            }

            menus.add(new AuthorityMenuitem(menu.value(), action,
                getMenuRole(value), getMenuAuthority(value), menu.parent(), menu.order()));
        });

        authorityService.initializeAuthorityMenus(
            ImmutableList.copyOf(authorityOrdering.sortedCopy(packageMenu(menus))));
        log.info("initialized authority menus successful");
    }

    @CachePut(value = "_menus", key = "'_menus'")
    public List<AuthorityMenuitem> cachePut(List<AuthorityMenuitem> finalMenus) {
        return finalMenus;
    }

    private String getMenuRole(HandlerMethod value) {
        RolesAllowed rolesAllowed = value.getMethodAnnotation(RolesAllowed.class);
        return null != rolesAllowed ? rolesAllowed.value()[0] : null;
    }

    private String getMenuAuthority(HandlerMethod value) {
        PreAuthorize preAuthorize = value.getMethodAnnotation(PreAuthorize.class);
        if (null == preAuthorize) {
            return null;
        }

        return authories.stream()
            .filter(auth -> preAuthorize.value().contains(String.format(authorityFormat, auth)))
            .findFirst()
            .orElse(null)
            ;
    }

    private List<AuthorityMenuitem> packageMenu(List<AuthorityMenuitem> menus) {
        List<AuthorityMenuitem> packagedMenus = menus.stream()
            .filter(m -> isNullOrEmpty(m.getParent()))
            .collect(Collectors.toList())
            ;

        packagedMenus.forEach(menu -> packagingTo(menu, menus));
        return packagedMenus;
    }

    private void packagingTo(AuthorityMenuitem menu, List<AuthorityMenuitem> menus) {
        List<AuthorityMenuitem> child = Lists.newArrayList();
        menus.forEach(m -> {
            if (m.getParent().equals(menu.getTitle())) {
                packagingTo(m, menus);
                child.add(m);
            }
        });

        if (child.size() > 0) {
            menu.setChild(authorityOrdering.sortedCopy(child));
        }
    }

    private static final List<String> authories;
    private static final String authorityFormat = "'%s'";
    private static final Ordering<AuthorityMenuitem> authorityOrdering = new Ordering<AuthorityMenuitem>() {
        @Override
        public int compare(@Nullable AuthorityMenuitem left, @Nullable AuthorityMenuitem right) {
            return Ints.compare(checkNotNull(left).getOrder(), checkNotNull(right).getOrder());
        }
    };

    static {
        authories = Lists.newArrayList();

        Arrays
            .stream(Authorities.class.getDeclaredFields())
            .forEach(field -> authories.add(field.getName().toLowerCase()));
    }
}
