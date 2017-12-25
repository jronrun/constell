package com.benayn.constell.services.capricorn.repository.handlers;

import com.benayn.constell.service.mybatis.handler.AbstractPgJsonTypeHandler;
import com.benayn.constell.services.capricorn.type.AccessBy;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(AccessBy.class)
public class AccessByTypeHandler extends AbstractPgJsonTypeHandler<AccessBy> {

}
