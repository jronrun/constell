package com.benayn.constell.service.server.service;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.Renderable;

public interface Service<T> {

    T selectById(long entityId);

    Page<T> selectPageBy(Renderable condition);

    /**
     * Delete permanently, unrecoverable
     */
    int deleteById(Long entityId) throws ServiceException;

    int save(Renderable entity) throws ServiceException;

}
