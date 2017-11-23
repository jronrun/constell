package com.benayn.constell.service.server.menu;

import static com.google.common.base.MoreObjects.toStringHelper;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorityMenuBread extends MenuBread {

    private String role;
    private String authority;
    private String parent;
    private int order;
    private String group;
    private int groupOrder;
    private List<AuthorityMenuBread> child = Lists.newArrayList();

    public MenuBread asMenu(boolean authorized) {
        MenuBread menu = new MenuBread();
        menu.setId(getId());
        menu.setTitle(getTitle());
        menu.setAction(getAction());
        menu.setAuthorized(authorized);
        menu.setFresh(isFresh());
        menu.setIcon(getIcon());
        return menu;
    }

    public boolean hasChild() {
        return getChild().size() > 0;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
            .addValue(super.toString())
            .add("parent", getParent())
            .add("order", getOrder())
            .add("role", getRole())
            .add("authority", getAuthority())
            .add("group", getGroup())
            .add("groupOrder", getGroupOrder())
            .toString();
    }
}
