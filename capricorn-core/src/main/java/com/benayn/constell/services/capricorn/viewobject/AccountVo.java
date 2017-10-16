package com.benayn.constell.services.capricorn.viewobject;

import com.benayn.constell.service.enums.Gender;
import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.Creatable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.HtmlTag;
import com.benayn.constell.service.server.respond.InputType;
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

    @NotNull
    @DefineElement("render.account.password")
    @Creatable
    private String password;

    @NotNull
    @DefineElement("render.account.gender")
    @Listable
    @Editable(type = InputType.RADIO, options = Gender.class)
    private Short gender;

    @DefineElement("render.account.enabled")
    @Listable
    @Editable(tag = HtmlTag.ON_OFF)
    private boolean enabled;

    @DefineElement("render.account.credentials.expired")
    @Listable
    @Editable(tag = HtmlTag.ON_OFF)
    private boolean credentialsExpired;

    @DefineElement("render.account.expired")
    @Listable
    @Editable(tag = HtmlTag.ON_OFF)
    private boolean expired;

    @DefineElement("render.account.locked")
    @Listable
    @Editable(tag = HtmlTag.ON_OFF)
    private boolean locked;

    @NotNull
    @DefineElement(value = "render.account.status", options = AccountStatus.class)
    @Searchable
    @Listable
    @Editable(tag = HtmlTag.SELECT)
    private Short status;

    @Listable("render.common.createTime")
    private String createTime;

}
