package com.benayn.constell.services.capricorn.service.bean;

import static com.benayn.constell.service.util.Assets.checkRecordDeleted;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.service.GenericService;
import com.benayn.constell.services.capricorn.enums.ContentStatus;
import com.benayn.constell.services.capricorn.repository.ContentRepository;
import com.benayn.constell.services.capricorn.repository.domain.Content;
import com.benayn.constell.services.capricorn.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentServiceBean extends GenericService<Content> implements ContentService {

    private ContentRepository contentRepository;

    @Autowired
    public ContentServiceBean(ContentRepository contentRepository) {
        super(contentRepository);
        this.contentRepository = contentRepository;
    }

    @Override
    public int deleteByContentId(Long entityId) throws ServiceException {
        Content delete = new Content();
        delete.setId(entityId);
        delete.setStatus(ContentStatus.DELETED.getValue());
        return checkRecordDeleted(contentRepository.updateById(delete));
    }
}
