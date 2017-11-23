package com.benayn.constell.service.server.menu;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuBread {

    private String id;
    private String title;
    private String action;
    private boolean authorized;
    private boolean fresh;
    private String icon;

    private List<MenuBread> children = Lists.newArrayList();

}
