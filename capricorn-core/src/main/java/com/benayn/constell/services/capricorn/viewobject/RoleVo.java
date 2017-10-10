package com.benayn.constell.services.capricorn.viewobject;

import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.service.server.respond.TagName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Actionable
public class RoleVo extends Renderable {

    @DefineElement("render.common.id")
    @Listable
    @Editable(hidden = true)
    private Long id;

    @DefineElement("render.role.code")
    @Searchable(like = true)
    @Listable
    @Editable
    private String code;

    @DefineElement("render.role.label")
    @Searchable(like = true)
    @Listable
    @Editable(tag = TagName.TEXTAREA, attributes = {"rows=3"})
    private String label;

    @Listable("render.common.createTime")
    private String createTime;

}
