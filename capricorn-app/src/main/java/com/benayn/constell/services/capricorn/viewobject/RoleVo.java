package com.benayn.constell.services.capricorn.viewobject;

import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RoleVo extends Renderable {

    @DefineElement("render.common.id")
    @Listable
    @Editable(hidden = true)
    private Long id;

    @DefineElement(value = "render.role.code", attributes = {"src=test,abcd=abcd"})
    @Searchable(like = true)
    @Listable
    @Editable
    private String code;

    @DefineElement("render.role.label")
    @Searchable(like = true)
    @Listable
    @Editable
    private String label;

    @Listable("render.common.createTime")
    private Date createTime;

}
