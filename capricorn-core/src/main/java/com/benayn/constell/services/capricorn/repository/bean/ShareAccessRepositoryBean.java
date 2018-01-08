package com.benayn.constell.services.capricorn.repository.bean;

import com.benayn.constell.service.server.repository.bean.GenericRepository;
import com.benayn.constell.services.capricorn.repository.ShareAccessRepository;
import com.benayn.constell.services.capricorn.repository.domain.ShareAccess;
import com.benayn.constell.services.capricorn.repository.domain.ShareAccessExample;
import com.benayn.constell.services.capricorn.repository.mapper.ShareAccessMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ShareAccessRepositoryBean extends GenericRepository<ShareAccess, ShareAccessExample, ShareAccessMapper> implements ShareAccessRepository {

}
