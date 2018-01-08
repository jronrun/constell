package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.service.server.service.GenericService;
import com.benayn.constell.services.capricorn.repository.ContentRepository;
import com.benayn.constell.services.capricorn.repository.domain.Content;
import com.benayn.constell.services.capricorn.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentServiceBean extends GenericService<Content> implements ContentService {

    @Autowired
    public ContentServiceBean(ContentRepository contentRepository) {
        super(contentRepository);
    }

}
