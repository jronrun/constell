package com.benayn.constell.services.capricorn.settings.listener;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import com.benayn.constell.service.server.menu.AuthorityMenuBread;
import com.benayn.constell.service.server.menu.AuthorityMenuGroup;
import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.service.server.menu.MenuGroup;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.service.AuthorityService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Ints;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
@Slf4j
public class ApplicationEventListener {

    private MessageSource messageSource;
    private AuthorityService authorityService;
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public ApplicationEventListener(MessageSource messageSource,
        AuthorityService authorityService, RequestMappingHandlerMapping handlerMapping) {
        this.messageSource = messageSource;
        this.authorityService = authorityService;
        this.handlerMapping = handlerMapping;
    }

    @EventListener
    public void onApplicationReadyEvent(ContextRefreshedEvent event) {
        List<AuthorityMenuBread> menus = Lists.newArrayList();
        handlerMapping.getHandlerMethods().forEach((key, value) -> {
            MenuCapability menu = value.getMethodAnnotation(MenuCapability.class);
            if (null == menu) {
                return;
            }

            String action;
            List<String> patterns = Lists.newArrayList(key.getPatternsCondition().getPatterns());
            if (patterns.isEmpty() || (action = patterns.get(0)).contains("{")) {
                log.warn("Unsupported menu capability {} : {}", menu.value(), key);
                return;
            }

            String menuTitle = getLocalText(menu.value());
            AuthorityMenuBread authorityMenuBread = new AuthorityMenuBread();
            authorityMenuBread.setTitle(menuTitle);
            authorityMenuBread.setAction(action);
            authorityMenuBread.setRole(getMenuRole(value));
            authorityMenuBread.setAuthority(getMenuAuthority(value));
            authorityMenuBread.setParent(getLocalText(menu.parent()));
            authorityMenuBread.setOrder(menu.order());
            authorityMenuBread.setFresh(menu.fresh());
            authorityMenuBread.setIcon(menu.icon());
            authorityMenuBread.setGroup(getLocalText(menu.group()));
            authorityMenuBread.setGroupOrder(menu.groupOrder());
            authorityMenuBread.setId(Hashing.crc32().hashString(menuTitle, UTF_8).toString());

            menus.add(authorityMenuBread);
        });

        authorityService.initializeMenuGroup(
            ImmutableList.copyOf(menuGroupOrdering.sortedCopy(packageMenuGroup(menus))));
        log.info("Initialized authority menus successful");
    }

    private String getLocalText(String value) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(value, null, value, locale);
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

        return authorities.stream()
            .filter(auth -> preAuthorize.value().contains(String.format(authorityFormat, auth)))
            .findFirst()
            .orElse(null)
            ;
    }

    private List<AuthorityMenuGroup> packageMenuGroup(List<AuthorityMenuBread> menus) {
        List<AuthorityMenuBread> packagedMenu = authorityOrdering.sortedCopy(packageMenu(menus));

        Map<String, AuthorityMenuGroup> theGroup = Maps.newHashMap();
        packagedMenu.forEach(menu -> {
            String aGroup = isNullOrEmpty(menu.getGroup()) ? DEFAULT_GROUP : menu.getGroup();
            if (DEFAULT_GROUP.equals(aGroup)) {
                menu.setGroupOrder(5);
            }

            if (theGroup.containsKey(aGroup)) {
                theGroup.get(aGroup).add(menu);
            } else {
                AuthorityMenuGroup menuGroup = new AuthorityMenuGroup();
                menuGroup.setOrder(menu.getGroupOrder());
                menuGroup.setTitle(menu.getGroup());
                menuGroup.setAuthorityMenus(Lists.newArrayList(menu));
                theGroup.put(aGroup, menuGroup);
            }
        });

        return Lists.newArrayList(theGroup.values());
    }

    private List<AuthorityMenuBread> packageMenu(List<AuthorityMenuBread> menus) {
        List<AuthorityMenuBread> packagedMenus = menus.stream()
            .filter(m -> isNullOrEmpty(m.getParent()))
            .collect(Collectors.toList())
            ;

        packagedMenus.forEach(menu -> packagingTo(menu, menus));
        return packagedMenus;
    }

    private void packagingTo(AuthorityMenuBread menu, List<AuthorityMenuBread> menus) {
        List<AuthorityMenuBread> child = Lists.newArrayList();
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

    private static final String DEFAULT_GROUP = "default";
    private static final List<String> authorities;
    private static final String authorityFormat = "'%s'";
    private static final Ordering<AuthorityMenuBread> authorityOrdering = new Ordering<AuthorityMenuBread>() {
        @Override
        public int compare(@Nullable AuthorityMenuBread left, @Nullable AuthorityMenuBread right) {
            return Ints.compare(checkNotNull(left).getOrder(), checkNotNull(right).getOrder());
        }
    };

    private static final Ordering<MenuGroup> menuGroupOrdering = new Ordering<MenuGroup>() {
        @Override
        public int compare(@Nullable MenuGroup left, @Nullable MenuGroup right) {
            return Ints.compare(checkNotNull(left).getOrder(), checkNotNull(right).getOrder());
        }
    };

    static {
        authorities = Lists.newArrayList();

        Arrays
            .stream(Authorities.class.getDeclaredFields())
            .forEach(field -> authorities.add(field.getName().toLowerCase()));
    }
}
