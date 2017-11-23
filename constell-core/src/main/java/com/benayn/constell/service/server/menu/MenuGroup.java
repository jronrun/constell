package com.benayn.constell.service.server.menu;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuGroup {

    private String title;
    private int order;
    private List<MenuBread> menus;

}
