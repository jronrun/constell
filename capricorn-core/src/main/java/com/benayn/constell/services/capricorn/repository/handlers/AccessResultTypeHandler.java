package com.benayn.constell.services.capricorn.repository.handlers;

import com.benayn.constell.service.mybatis.handler.AbstractPgJsonTypeHandler;
import com.benayn.constell.services.capricorn.type.AccessResult;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(AccessResult.class)
public class AccessResultTypeHandler extends AbstractPgJsonTypeHandler<AccessResult> {

}
