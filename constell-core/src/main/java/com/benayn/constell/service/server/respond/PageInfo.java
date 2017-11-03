package com.benayn.constell.service.server.respond;

import lombok.Data;

@Data
public class PageInfo {

    /**
     * render.%s.module.title, placeholder is {@link PageInfo#module}
     */
    private String title;
    /**
     * generator with given sub class simple name of {@link Renderable} and format to lowercase and trim last 'Vo'
     */
    private String module;
    private String pageId;
    private String searchId;
    private String contentId;
    private String editId;
    private String index;
    private String list;
    private String retrieve;
    private String create;
    private String update;
    private String delete;

}
