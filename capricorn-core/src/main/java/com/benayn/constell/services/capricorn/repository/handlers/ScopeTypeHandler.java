package com.benayn.constell.services.capricorn.repository.handlers;

import com.benayn.constell.service.mybatis.handler.AbstractPgJsonTypeHandler;
import com.benayn.constell.services.capricorn.type.Scope;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Scope.class)
public class ScopeTypeHandler extends AbstractPgJsonTypeHandler<Scope> {

}
