package com.benayn.constell.service.server.menu;

import static java.util.Optional.ofNullable;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorityMenuGroup extends MenuGroup {

    List<AuthorityMenuBread> authorityMenus;

    public void add(AuthorityMenuBread authorityMenu) {
        ofNullable(authorityMenus).orElse(Lists.newArrayList()).add(authorityMenu);
    }

    public MenuGroup asMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setTitle(getTitle());
        menuGroup.setOrder(getOrder());
        menuGroup.setMenus(getMenus());
        return menuGroup;
    }

}
