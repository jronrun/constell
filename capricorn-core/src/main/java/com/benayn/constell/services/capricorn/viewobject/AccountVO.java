package com.benayn.constell.services.capricorn.viewobject;

import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.LIKE;
import static com.benayn.constell.service.server.respond.HtmlTag.SELECT;
import static com.benayn.constell.service.server.respond.HtmlTag.TOGGLE;
import static com.benayn.constell.service.server.respond.InputType.PASSWORD;
import static com.benayn.constell.service.server.respond.InputType.RADIO;
import static com.benayn.constell.services.capricorn.config.Authorities.ACCOUNT_CREATE;
import static com.benayn.constell.services.capricorn.config.Authorities.ACCOUNT_DELETE;
import static com.benayn.constell.services.capricorn.config.Authorities.ACCOUNT_RETRIEVE;
import static com.benayn.constell.services.capricorn.config.Authorities.ACCOUNT_UPDATE;
import static com.benayn.constell.services.capricorn.config.Authorities.MODEL_ROLE_INDEX;
import static com.benayn.constell.services.capricorn.config.Authorities.RELATION_ACCOUNT_ROLE_CREATE;
import static com.benayn.constell.services.capricorn.config.Authorities.RELATION_ACCOUNT_ROLE_DELETE;

import com.benayn.constell.service.enums.Gender;
import com.benayn.constell.service.server.respond.Accessable;
import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.Creatable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.DefineTouch;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.service.server.respond.TouchAccessable;
import com.benayn.constell.service.server.respond.Touchable;
import com.benayn.constell.service.server.respond.Updatable;
import com.benayn.constell.services.capricorn.enums.AccountStatus;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;

@Getter
@Setter
@ToString
@Actionable(editField = "username", appendFragment = "account_list_append_action", readyFragment = "account_page_ready")
@DefineTouch(name = "render.role.touch.role", view = RoleVO.class, master = true, accessable = @TouchAccessable(
    index = MODEL_ROLE_INDEX,
    create = RELATION_ACCOUNT_ROLE_CREATE,
    delete = RELATION_ACCOUNT_ROLE_DELETE
))
@Accessable(
    create = ACCOUNT_CREATE,
    retrieve = ACCOUNT_RETRIEVE,
    update = ACCOUNT_UPDATE,
    delete = ACCOUNT_DELETE)
public class AccountVO extends Renderable {

    @DefineElement("render.common.id")
    @Searchable
    @Listable
    @Touchable
    @Editable(hidden = true)
    private Long id;

    @NotNull
    @DefineElement("render.account.username")
    @Searchable(condition = LIKE)
    @Listable
    @Touchable
    @Editable
    private String username;

    @NotNull
    @Email
    @DefineElement("render.account.email")
    @Searchable(condition = LIKE)
    @Listable
    @Touchable
    @Updatable(readonly = true, disabled = true)
    @Creatable
    private String email;

    @DefineElement("render.account.password")
    @Creatable(type = PASSWORD)
    private String password;

    @DefineElement("render.account.password.retype")
    @Creatable(type = PASSWORD)
    private String password2;

    @NotNull
    @DefineElement(value = "render.account.gender", options = Gender.class)
    @Listable(fragment = "account_list_gender")
    @Touchable(fragment = "account_list_gender")
    @Editable(type = RADIO)
    private Short gender;

    @DefineElement("render.account.enabled")
    @Listable(toggleWidget = true)
    @Touchable(toggleWidget = true)
    @Editable(tag = TOGGLE)
    private boolean enabled;

    @DefineElement("render.account.credentials.expired")
    @Listable(toggleWidget = true)
    @Touchable(toggleWidget = true)
    @Editable(tag = TOGGLE)
    private boolean credentialsExpired;

    @DefineElement("render.account.expired")
    @Listable(toggleWidget = true)
    @Touchable(toggleWidget = true)
    @Editable(tag = TOGGLE)
    private boolean expired;

    @DefineElement("render.account.locked")
    @Listable(toggleWidget = true)
    @Touchable(toggleWidget = true)
    @Editable(tag = TOGGLE)
    private boolean locked;

    @DefineElement(value = "render.account.status", options = AccountStatus.class, tag = SELECT)
    @Searchable
    @Listable
    @Touchable
    private Short status;

    @Listable("render.common.createTime")
    @Touchable("render.common.createTime")
    private String createTime;

}
