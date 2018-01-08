package com.benayn.constell.services.capricorn.controller.manage;


import static com.benayn.constell.service.server.respond.Responds.success;
import static com.benayn.constell.service.util.LZString.decodes;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.repository.domain.ShareAccess;
import com.benayn.constell.services.capricorn.service.ShareAccessService;
import com.benayn.constell.services.capricorn.settings.constant.Menus;
import com.benayn.constell.services.capricorn.viewobject.ShareAccessVO;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = MANAGE_BASE)
public class ShareAccessController extends BaseManageController<ShareAccessVO> {

    private ShareAccessService shareAccessService;

    @Autowired
    public ShareAccessController(ShareAccessService shareAccessService) {
        this.shareAccessService = shareAccessService;
    }

    @MenuCapability(value = Menus.SHARE_ACCESS_MANAGE, parent = Menus.UGC)
    @PreAuthorize(Authorities.SHARE_ACCESS_INDEX)
    @GetMapping("share-access/index")
    public String index(Authentication authentication, Model model,
        @RequestHeader(value = "condition", required = false) String condition,
        @RequestHeader(value = "touchable", required = false) Boolean touchable) {
        return genericIndex(authentication, model, touchable, decodes(condition, ShareAccessVO.class));
    }

    @PreAuthorize(Authorities.SHARE_ACCESS_INDEX)
    @GetMapping("share-accesses")
    public String items(Authentication authentication, Model model, ShareAccessVO condition) {
        return genericList(authentication, model, shareAccessService.selectPageBy(condition), condition);
    }

    @PreAuthorize(Authorities.SHARE_ACCESS_RETRIEVE)
    @GetMapping(value = "share-access/{entityId}")
    public String retrieve(Authentication authentication, Model model, @PathVariable("entityId") Long entityId) {
        ShareAccess item = null;
        if (entityId > 0) {
            item = shareAccessService.selectById(entityId);
        }

        return genericEdit(authentication, model, item);
    }

    @PreAuthorize(Authorities.SHARE_ACCESS_CREATE)
    @PostMapping(value = "share-access", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> create(@Valid @RequestBody ShareAccessVO entity) throws ServiceException {
        return success(shareAccessService.save(entity));
    }

    @PreAuthorize(Authorities.SHARE_ACCESS_UPDATE)
    @PutMapping(value = "share-access", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> update(@Valid @RequestBody ShareAccessVO entity) throws ServiceException {
        return success(shareAccessService.save(entity));
    }

    @PreAuthorize(Authorities.SHARE_ACCESS_DELETE)
    @DeleteMapping(value = "share-access/{entityId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> delete(@PathVariable("entityId") Long entityId) throws ServiceException {
        return success(shareAccessService.deleteById(entityId));
    }

}
