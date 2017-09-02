package com.benayn.constell.service.server.menu;

import static com.google.common.base.MoreObjects.toStringHelper;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorityMenuitem extends Menuitem {

    private String role;
    private String authority;
    private String parent;
    private int order;
    private List<AuthorityMenuitem> child = Lists.newArrayList();

    public AuthorityMenuitem(String title, String action, String role, String authority, String parent, int order) {
        super(title, action);
        this.role = role;
        this.authority = authority;
        this.parent = parent;
        this.order = order;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
            .add("id", getId())
            .add("title", getTitle())
            .add("parent", getParent())
            .add("order", getOrder())
            .add("action", getAction())
            .add("role", getRole())
            .add("authority", getAuthority())
            .toString();
    }
}
