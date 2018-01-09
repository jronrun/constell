package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.service.server.respond.Responds.success;
import static com.benayn.constell.service.util.LZString.decodes;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.enums.ContentStatus;
import com.benayn.constell.services.capricorn.repository.domain.Content;
import com.benayn.constell.services.capricorn.service.ContentService;
import com.benayn.constell.services.capricorn.settings.constant.Menus;
import com.benayn.constell.services.capricorn.viewobject.ContentVO;
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
public class ContentController extends BaseManageController<ContentVO> {

    private ContentService contentService;

    @Autowired
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @MenuCapability(value = Menus.CONTENT_MANAGE, parent = Menus.UGC)
    @PreAuthorize(Authorities.CONTENT_INDEX)
    @GetMapping("content/index")
    public String index(Authentication authentication, Model model,
        @RequestHeader(value = "condition", required = false) String condition,
        @RequestHeader(value = "touchable", required = false) Boolean touchable) {
        return genericIndex(authentication, model, touchable, decodes(condition, ContentVO.class));
    }

    @PreAuthorize(Authorities.CONTENT_INDEX)
    @GetMapping("contents")
    public String items(Authentication authentication, Model model, ContentVO condition) {
        condition.setStatus(ContentStatus.USING.getValue());
        return genericList(authentication, model, contentService.selectPageBy(condition), condition);
    }

    @PreAuthorize(Authorities.CONTENT_RETRIEVE)
    @GetMapping(value = "content/{entityId}")
    public String retrieve(Authentication authentication, Model model, @PathVariable("entityId") Long entityId) {
        Content item = null;
        if (entityId > 0) {
            item = contentService.selectById(entityId);
        }

        return genericEdit(authentication, model, item);
    }

    @PreAuthorize(Authorities.CONTENT_CREATE)
    @PostMapping(value = "content", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> create(@Valid @RequestBody ContentVO entity) throws ServiceException {
        entity.setStatus(ContentStatus.USING.getValue());
        return success(contentService.save(entity));
    }

    @PreAuthorize(Authorities.CONTENT_UPDATE)
    @PutMapping(value = "content", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> update(@Valid @RequestBody ContentVO entity) throws ServiceException {
        return success(contentService.save(entity));
    }

    @PreAuthorize(Authorities.CONTENT_DELETE)
    @DeleteMapping(value = "content/{entityId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> delete(@PathVariable("entityId") Long entityId) throws ServiceException {
        return success(contentService.deleteByContentId(entityId));
    }

}
