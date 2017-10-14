package com.benayn.constell.services.capricorn.viewobject;

import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.HtmlTag;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Actionable(createFragment = "permission_create", delete = false)
public class PermissionVo extends Renderable {

    @DefineElement("render.common.id")
    @Listable
    @Editable(hidden = true)
    private Long id;

    @NotNull
    @Size(min = 1)
    @DefineElement("render.permission.code")
    @Searchable(like = true)
    @Listable
    @Editable(readonly = true, disabled = true)
    private String code;

    @NotNull
    @Size(min = 1)
    @DefineElement("render.permission.label")
    @Searchable(like = true)
    @Listable
    @Editable(tag = HtmlTag.TEXTAREA, attributes = {"rows=3"})
    private String label;

    @Listable("render.common.createTime")
    private String createTime;

}
