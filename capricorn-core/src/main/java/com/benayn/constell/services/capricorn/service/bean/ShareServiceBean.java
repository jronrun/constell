package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.service.server.service.GenericService;
import com.benayn.constell.services.capricorn.repository.ShareRepository;
import com.benayn.constell.services.capricorn.repository.domain.Share;
import com.benayn.constell.services.capricorn.service.ShareService;
import org.springframework.stereotype.Service;

@Service
public class ShareServiceBean extends GenericService<Share> implements ShareService {

    public ShareServiceBean(ShareRepository shareRepository) {
        super(shareRepository);
    }
}
