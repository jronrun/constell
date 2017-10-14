package com.benayn.constell.services.capricorn.viewobject;

import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.service.server.respond.HtmlTag;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Actionable(delete = false)
public class RoleVo extends Renderable {

    @DefineElement("render.common.id")
    @Listable
    @Editable(hidden = true)
    private Long id;

    @NotNull
    @Pattern(regexp = "^ROLE_.*", message = "{valid.role.code.pattern}")
    @DefineElement("render.role.code")
    @Searchable(like = true)
    @Listable
    @Editable
    private String code;

    @NotNull
    @Size(min = 1)
    @DefineElement("render.role.label")
    @Searchable(like = true)
    @Listable
    @Editable(tag = HtmlTag.TEXTAREA, attributes = {"rows=3"})
    private String label;

    @Listable("render.common.createTime")
    private String createTime;

}
