package com.benayn.constell.services.capricorn.repository.handlers;

import com.benayn.constell.service.mybatis.handler.AbstractPgJsonTypeHandler;
import com.benayn.constell.services.capricorn.type.Language;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Language.class)
public class LanguageTypeHandler extends AbstractPgJsonTypeHandler<Language> {

}
