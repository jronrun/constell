package com.benayn.constell.services.capricorn.repository.bean;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.ContentRepository;
import com.benayn.constell.services.capricorn.repository.domain.Content;
import com.benayn.constell.services.capricorn.repository.domain.ContentExample;
import com.benayn.constell.services.capricorn.repository.mapper.ContentMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ContentRepositoryBean extends GenericRepository<Content, ContentExample, ContentMapper> implements ContentRepository {

}
