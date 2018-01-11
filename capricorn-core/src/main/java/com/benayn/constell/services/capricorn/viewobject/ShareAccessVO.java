package com.benayn.constell.services.capricorn.viewobject;

import static com.benayn.constell.services.capricorn.config.Authorities.SHARE_ACCESS_CREATE;
import static com.benayn.constell.services.capricorn.config.Authorities.SHARE_ACCESS_DELETE;
import static com.benayn.constell.services.capricorn.config.Authorities.SHARE_ACCESS_RETRIEVE;
import static com.benayn.constell.services.capricorn.config.Authorities.SHARE_ACCESS_UPDATE;

import com.benayn.constell.service.server.respond.Accessable;
import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.services.capricorn.type.AccessBy;
import com.benayn.constell.services.capricorn.type.AccessResult;
import com.benayn.constell.services.capricorn.type.ShareSnapshoot;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Actionable(create = false, edit = false, delete = false)
@Accessable(
    create = SHARE_ACCESS_CREATE,
    retrieve = SHARE_ACCESS_RETRIEVE,
    update = SHARE_ACCESS_UPDATE,
    delete = SHARE_ACCESS_DELETE)
public class ShareAccessVO extends Renderable {

    @DefineElement("render.common.id")
    @Searchable
    @Listable
    @Editable(hidden = true)
    private Long id;

    @DefineElement("render.shareAccess.shareSnapshoot")
    private ShareSnapshoot shareSnapshoot;

    @DefineElement("render.shareAccess.contentSnapshoot")
    private String contentSnapshoot;

    @DefineElement("render.shareAccess.accessBy")
    private AccessBy accessBy;

    @DefineElement("render.shareAccess.accessResult")
    private AccessResult accessResult;

    @DefineElement("render.common.createTime")
    @Listable
    private String createTime;

}
