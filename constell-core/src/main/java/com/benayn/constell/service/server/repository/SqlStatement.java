package com.benayn.constell.service.server.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum  SqlStatement {
    COUNT_BY_EXAMPLE("countByExample"),
    DELETE_BY_EXAMPLE("deleteByExample"),
    DELETE_BY_PRIMARYKEY("deleteByPrimaryKey"),
    INSERT("insert"),
    INSERT_SELECTIVE("insertSelective"),
    SELECT_BY_EXAMPLE("selectByExample"),
    SELECT_BY_PRIMARY_KEY("selectByPrimaryKey"),
    UPDATE_BY_EXAMPLE_SELECTIVE("updateByExampleSelective"),
    UPDATE_BY_EXAMPLE("updateByExample"),
    UPDATE_BY_PRIMARY_KEY_SELECTIVE("updateByPrimaryKeySelective"),
    UPDATE_BY_PRIMARY_KEY("updateByPrimaryKey")
    ;

    @Getter
    private final String statement;

}
