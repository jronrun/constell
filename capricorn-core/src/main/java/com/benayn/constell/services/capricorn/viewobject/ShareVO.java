package com.benayn.constell.services.capricorn.viewobject;

import static com.benayn.constell.service.server.respond.HtmlTag.SELECT;
import static com.benayn.constell.services.capricorn.config.Authorities.SHARE_CREATE;
import static com.benayn.constell.services.capricorn.config.Authorities.SHARE_DELETE;
import static com.benayn.constell.services.capricorn.config.Authorities.SHARE_RETRIEVE;
import static com.benayn.constell.services.capricorn.config.Authorities.SHARE_UPDATE;

import com.benayn.constell.service.server.respond.Accessable;
import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.services.capricorn.enums.ShareAuthority;
import com.benayn.constell.services.capricorn.enums.ShareState;
import com.benayn.constell.services.capricorn.enums.ShareType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Actionable(create = false, delete = false)
@Accessable(
    create = SHARE_CREATE,
    retrieve = SHARE_RETRIEVE,
    update = SHARE_UPDATE,
    delete = SHARE_DELETE)
public class ShareVO extends Renderable {

    @DefineElement("render.common.id")
    @Searchable
    @Listable
    @Editable(hidden = true)
    private Long id;

    @DefineElement("render.share.title")
    @Searchable
    @Listable
    @Editable
    private String title;

    @DefineElement("render.share.path")
    @Listable
    @Editable
    private String path;

    @DefineElement(value = "render.share.type", options = ShareType.class, tag = SELECT)
    @Editable
    private Short type;

    @DefineElement(value = "render.share.authority", options = ShareAuthority.class, tag = SELECT)
    @Editable
    private Short authority;

    @DefineElement("render.share.openCount")
    @Editable
    private Long openCount;

    @DefineElement("render.share.usedCount")
    @Editable
    private Long usedCount;

    @DefineElement("render.common.startTime")
    @Editable
    private String startTime;

    @DefineElement("render.common.endTime")
    @Editable
    private String endTime;

    @DefineElement("render.share.scope")
    @Editable
    private ScopeVO scope;

    @DefineElement(value = "render.common.state", options = ShareState.class, tag = SELECT)
    @Searchable
    @Listable
    @Editable
    private Short state;

    @DefineElement("render.common.createTime")
    @Listable
    private String createTime;

}
