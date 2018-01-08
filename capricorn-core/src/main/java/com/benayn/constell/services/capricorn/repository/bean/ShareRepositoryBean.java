package com.benayn.constell.services.capricorn.repository.bean;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.ShareRepository;
import com.benayn.constell.services.capricorn.repository.domain.Share;
import com.benayn.constell.services.capricorn.repository.domain.ShareExample;
import com.benayn.constell.services.capricorn.repository.mapper.ShareMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ShareRepositoryBean extends GenericRepository<Share, ShareExample, ShareMapper> implements ShareRepository {

}
