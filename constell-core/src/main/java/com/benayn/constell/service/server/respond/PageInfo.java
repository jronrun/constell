package com.benayn.constell.service.server.respond;

import lombok.Data;

@Data
public class PageInfo {

    /**
     * render.%s.module.title
     */
    private String title;
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
