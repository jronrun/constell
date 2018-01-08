package com.benayn.constell.services.capricorn.repository.bean;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.TagRepository;
import com.benayn.constell.services.capricorn.repository.domain.Tag;
import com.benayn.constell.services.capricorn.repository.domain.TagExample;
import com.benayn.constell.services.capricorn.repository.mapper.TagMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TagRepositoryBean extends GenericRepository<Tag, TagExample, TagMapper> implements TagRepository {

}
