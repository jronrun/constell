package com.benayn.constell.service.server.menu;

import static com.google.common.base.Charsets.UTF_8;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Menuitem {

    private String id;
    private String title;
    private String action;

    private List<Menuitem> children = Lists.newArrayList();

    public Menuitem(String title, String action) {
        this.title = title;
        this.action = action;
        this.id = Hashing.crc32().hashString(this.title, UTF_8).toString();
    }

}
