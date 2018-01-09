package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.service.server.service.CheckSavedEntity;
import com.benayn.constell.service.server.service.GenericService;
import com.benayn.constell.service.server.service.SaveEntity;
import com.benayn.constell.services.capricorn.repository.TagRepository;
import com.benayn.constell.services.capricorn.repository.domain.Tag;
import com.benayn.constell.services.capricorn.repository.domain.TagExample;
import com.benayn.constell.services.capricorn.service.TagService;
import org.springframework.stereotype.Service;

@Service
public class TagServiceBean extends GenericService<Tag> implements TagService {

    private TagRepository tagRepository;

    public TagServiceBean(TagRepository tagRepository) {
        super(tagRepository);
        this.tagRepository = tagRepository;
    }

    @Override
    protected CheckSavedEntity<Tag> checkIfExistWhenSave(SaveEntity<Tag> saveEntity) {
        Tag entity = saveEntity.getEntity();
        TagExample example = new TagExample();
        example.createCriteria().andCodeEqualTo(entity.getCode());
        Tag savedTag = tagRepository.selectOne(example);
        return CheckSavedEntity.of(savedTag, String.format("%s (%s)", entity.getLabel(), entity.getCode()));
    }
}
