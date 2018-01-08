package com.benayn.constell.service.server.repository.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ConditionTemplate {
    IS_NULL("%s is null"),
    IS_NOT_NULL("%s is not null"),
    EQUAL_TO("%s ="),
    NOT_EQUAL_TO("%s <>"),
    GREATER_THAN("%s >"),
    GREATER_THAN_OR_EQUAL_TO("%s >="),
    LESS_THAN("%s <"),
    LESS_THAN_OR_EQUAL_TO("%s <="),
    LIKE("%s like"),
    NOT_LIKE("%s not like"),
    IN("%s in"),
    NOT_IN("%s not in"),
    BETWEEN("%s between"),
    NOT_BETWEEN("%s not between")
    ;

    @Getter
    private final String template;

    public boolean isLikeTemplate() {
        return this == LIKE || this == NOT_LIKE;
    }

    public boolean isBetweenTemplate() {
        return this == BETWEEN || this == NOT_BETWEEN;
    }

    public String value(String property) {
        return String.format(getTemplate(), property);
    }

}
