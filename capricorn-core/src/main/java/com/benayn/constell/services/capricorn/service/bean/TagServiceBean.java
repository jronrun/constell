package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.service.server.service.GenericService;
import com.benayn.constell.services.capricorn.repository.TagRepository;
import com.benayn.constell.services.capricorn.repository.domain.Tag;
import com.benayn.constell.services.capricorn.service.TagService;
import org.springframework.stereotype.Service;

@Service
public class TagServiceBean extends GenericService<Tag> implements TagService {

    public TagServiceBean(TagRepository tagRepository) {
        super(tagRepository);
    }
}
