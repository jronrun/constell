package com.benayn.constell.services.capricorn.viewobject;

import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.DefineTouch;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.HtmlTag;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.service.server.respond.Touchable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Actionable(delete = false, relations = {
    @DefineTouch(name = "render.role.touch.permission", view = PermissionVo.class, master = true),
    @DefineTouch(name = "render.role.touch.account", view = AccountVo.class)
})
public class RoleVo extends Renderable {

    @DefineElement("render.common.id")
    @Listable
    @Editable(hidden = true)
    @Touchable
    private Long id;

    @NotNull
    @Pattern(regexp = "^ROLE_.*", message = "{valid.role.code.pattern}")
    @DefineElement("render.role.code")
    @Searchable(like = true)
    @Listable
    @Editable
    @Touchable
    private String code;

    @NotNull
    @Size(min = 1)
    @DefineElement("render.role.label")
    @Searchable(like = true)
    @Listable
    @Editable(tag = HtmlTag.TEXTAREA, attributes = {"rows=3"})
    @Touchable
    private String label;

    @DefineElement("render.common.createTime")
    @Listable
    @Touchable
    private String createTime;

}
