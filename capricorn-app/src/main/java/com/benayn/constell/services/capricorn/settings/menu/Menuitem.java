package com.benayn.constell.services.capricorn.settings.menu;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Menuitem {

    @NonNull
    private String title;
    @NonNull
    private String action;

    private List<Menuitem> children = Lists.newArrayList();

    public void addChildren(Menuitem childrenMenuitem) {
        getChildren().add(childrenMenuitem);
    }
}
