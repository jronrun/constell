package com.benayn.constell.services.capricorn.condition;

import com.benayn.constell.service.server.respond.QueryCondition;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RoleCondition extends QueryCondition {

    private String code;
    private String label;

}
