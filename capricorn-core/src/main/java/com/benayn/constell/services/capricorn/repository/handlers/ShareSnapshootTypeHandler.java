package com.benayn.constell.services.capricorn.repository.handlers;

import com.benayn.constell.service.mybatis.handler.AbstractPgJsonTypeHandler;
import com.benayn.constell.services.capricorn.type.ShareSnapshoot;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(ShareSnapshoot.class)
public class ShareSnapshootTypeHandler extends AbstractPgJsonTypeHandler<ShareSnapshoot> {

}
