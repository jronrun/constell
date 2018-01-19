package com.benayn.constell.services.capricorn.viewobject;

import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.LIKE;
import static com.benayn.constell.services.capricorn.config.Authorities.MODEL_ROLE_INDEX;
import static com.benayn.constell.services.capricorn.config.Authorities.PERMISSION_CREATE;
import static com.benayn.constell.services.capricorn.config.Authorities.PERMISSION_DELETE;
import static com.benayn.constell.services.capricorn.config.Authorities.PERMISSION_RETRIEVE;
import static com.benayn.constell.services.capricorn.config.Authorities.PERMISSION_UPDATE;
import static com.benayn.constell.services.capricorn.config.Authorities.RELATION_ROLE_PERMISSION_CREATE;
import static com.benayn.constell.services.capricorn.config.Authorities.RELATION_ROLE_PERMISSION_DELETE;

import com.benayn.constell.service.server.repository.domain.ConditionTemplate;
import com.benayn.constell.service.server.respond.Accessable;
import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.DefineTouch;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.HtmlTag;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.service.server.respond.TouchAccessable;
import com.benayn.constell.service.server.respond.Touchable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Actionable(
    createFragment = "permission_create",
    delete = false)
@DefineTouch(
    name = "render.permission.touch.role",
    view = RoleVO.class,
    accessable = @TouchAccessable(
        index = MODEL_ROLE_INDEX,
        create = RELATION_ROLE_PERMISSION_CREATE,
        delete = RELATION_ROLE_PERMISSION_DELETE))
@Accessable(
    create = PERMISSION_CREATE,
    retrieve = PERMISSION_RETRIEVE,
    update = PERMISSION_UPDATE,
    delete = PERMISSION_DELETE)
public class PermissionVO extends Renderable {

    @DefineElement("render.common.id")
    @Listable
    @Editable(hidden = true)
    @Touchable
    private Long id;

    @NotNull
    @Size(min = 1)
    @DefineElement("render.permission.code")
    @Searchable(condition = LIKE)
    @Listable
    @Editable(readonly = true, disabled = true)
    @Touchable
    private String code;

    @NotNull
    @Size(min = 1)
    @DefineElement("render.permission.label")
    @Searchable(condition = LIKE)
    @Listable
    @Editable(tag = HtmlTag.TEXTAREA, attributes = {"rows=3"})
    @Touchable
    private String label;

    @Listable("render.common.createTime")
    @Touchable("render.common.createTime")
    private String createTime;

}
