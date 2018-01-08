package com.benayn.constell.services.capricorn.viewobject;

import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.LIKE;
import static com.benayn.constell.services.capricorn.config.Authorities.ACCOUNT_INDEX;
import static com.benayn.constell.services.capricorn.config.Authorities.MODEL_ROLE_CREATE;
import static com.benayn.constell.services.capricorn.config.Authorities.MODEL_ROLE_DELETE;
import static com.benayn.constell.services.capricorn.config.Authorities.MODEL_ROLE_RETRIEVE;
import static com.benayn.constell.services.capricorn.config.Authorities.MODEL_ROLE_UPDATE;
import static com.benayn.constell.services.capricorn.config.Authorities.PERMISSION_INDEX;
import static com.benayn.constell.services.capricorn.config.Authorities.RELATION_ACCOUNT_ROLE_CREATE;
import static com.benayn.constell.services.capricorn.config.Authorities.RELATION_ACCOUNT_ROLE_DELETE;
import static com.benayn.constell.services.capricorn.config.Authorities.RELATION_ROLE_PERMISSION_CREATE;
import static com.benayn.constell.services.capricorn.config.Authorities.RELATION_ROLE_PERMISSION_DELETE;

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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Actionable(delete = false, relations = {
    @DefineTouch(name = "render.role.touch.permission", view = PermissionVO.class, master = true, switchable = true,
        accessable = @TouchAccessable(
            index = PERMISSION_INDEX,
            create = RELATION_ROLE_PERMISSION_CREATE,
            delete = RELATION_ROLE_PERMISSION_DELETE
    )),
    @DefineTouch(name = "render.role.touch.account", view = AccountVO.class,
        accessable = @TouchAccessable(
            index = ACCOUNT_INDEX,
            create = RELATION_ACCOUNT_ROLE_CREATE,
            delete = RELATION_ACCOUNT_ROLE_DELETE
    ))
})
@Accessable(
    create = MODEL_ROLE_CREATE,
    retrieve = MODEL_ROLE_RETRIEVE,
    update = MODEL_ROLE_UPDATE,
    delete = MODEL_ROLE_DELETE)
public class RoleVO extends Renderable {

    @DefineElement("render.common.id")
    @Listable
    @Editable(hidden = true)
    @Touchable
    private Long id;

    @NotNull
    @Pattern(regexp = "^ROLE_.*", message = "{valid.role.code.pattern}")
    @DefineElement("render.role.code")
    @Searchable(condition = LIKE)
    @Listable
    @Editable
    @Touchable
    private String code;

    @NotNull
    @Size(min = 1)
    @DefineElement("render.role.label")
    @Searchable(condition = LIKE)
    @Listable
    @Editable(tag = HtmlTag.TEXTAREA, attributes = {"rows=3"})
    @Touchable
    private String label;

    @DefineElement("render.common.createTime")
    @Listable
    @Touchable
    private String createTime;

}
