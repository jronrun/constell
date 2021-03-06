package com.benayn.constell.services.capricorn.service;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.service.Service;
import com.benayn.constell.services.capricorn.repository.domain.Content;

public interface ContentService extends Service<Content> {

    int deleteByContentId(Long entityId) throws ServiceException;
}
