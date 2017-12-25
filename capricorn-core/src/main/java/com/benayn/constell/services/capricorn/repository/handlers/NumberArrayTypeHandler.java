package com.benayn.constell.services.capricorn.repository.handlers;

import com.benayn.constell.service.mybatis.handler.AbstractPgArrayTypeHandler;
import java.util.List;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(List.class)
public class NumberArrayTypeHandler extends AbstractPgArrayTypeHandler<Long> {

}
