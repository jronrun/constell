package com.benayn.constell.services.capricorn.viewobject;

import static com.benayn.constell.service.server.respond.HtmlTag.SELECT;
import static com.benayn.constell.service.server.respond.HtmlTag.TOGGLE;
import static com.benayn.constell.service.server.respond.InputType.PASSWORD;
import static com.benayn.constell.service.server.respond.InputType.RADIO;

import com.benayn.constell.service.enums.Gender;
import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.Creatable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
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
@Actionable
public class AccountVo extends Renderable {

    @DefineElement("render.common.id")
    @Searchable
    @Listable
    @Editable(hidden = true)
    private Long id;

    @NotNull
    @DefineElement("render.account.username")
    @Searchable(like = true)
    @Listable
    @Editable
    private String username;

    @NotNull
    @Email
    @DefineElement("render.account.email")
    @Searchable(like = true)
    @Listable
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
    @Listable
    @Editable(type = RADIO)
    private Short gender;

    @DefineElement("render.account.enabled")
    @Listable(toggleWidget = true)
    @Editable(tag = TOGGLE)
    private boolean enabled;

    @DefineElement("render.account.credentials.expired")
    @Listable(toggleWidget = true)
    @Editable(tag = TOGGLE)
    private boolean credentialsExpired;

    @DefineElement("render.account.expired")
    @Listable(toggleWidget = true)
    @Editable(tag = TOGGLE)
    private boolean expired;

    @DefineElement("render.account.locked")
    @Listable(toggleWidget = true)
    @Editable(tag = TOGGLE)
    private boolean locked;

    @NotNull
    @DefineElement(value = "render.account.status", options = AccountStatus.class)
    @Searchable
    @Listable
    @Editable(tag = SELECT)
    private Short status;

    @Listable("render.common.createTime")
    private String createTime;

}
