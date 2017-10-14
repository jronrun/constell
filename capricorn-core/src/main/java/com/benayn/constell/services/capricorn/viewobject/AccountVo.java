package com.benayn.constell.services.capricorn.viewobject;

import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Actionable
public class AccountVo extends Renderable {

    @DefineElement("render.common.id")
    @Listable
    @Editable(hidden = true)
    private Long id;

    private String username;

    private String email;

    private String password;

    private Short gender;

    private Short status;

    private Date createTime;

    private boolean enabled;

    private boolean credentialsExpired;

    private boolean expired;

    private boolean locked;

}
