package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.service.server.service.GenericService;
import com.benayn.constell.services.capricorn.repository.ShareAccessRepository;
import com.benayn.constell.services.capricorn.repository.domain.ShareAccess;
import com.benayn.constell.services.capricorn.service.ShareAccessService;
import org.springframework.stereotype.Service;

@Service
public class ShareAccessServiceBean extends GenericService<ShareAccess> implements ShareAccessService {

    public ShareAccessServiceBean(ShareAccessRepository shareAccessRepository) {
        super(shareAccessRepository);
    }
}
