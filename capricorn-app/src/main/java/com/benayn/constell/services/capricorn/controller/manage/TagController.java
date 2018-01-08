package com.benayn.constell.services.capricorn.controller.manage;


import static com.benayn.constell.service.server.respond.Responds.success;
import static com.benayn.constell.service.util.LZString.decodes;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.repository.domain.Tag;
import com.benayn.constell.services.capricorn.service.TagService;
import com.benayn.constell.services.capricorn.settings.constant.Menus;
import com.benayn.constell.services.capricorn.viewobject.TagVO;
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
public class TagController extends BaseManageController<TagVO> {

    private TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @MenuCapability(value = Menus.TAG_MANAGE, parent = Menus.UGC)
    @PreAuthorize(Authorities.TAG_INDEX)
    @GetMapping("tag/index")
    public String index(Authentication authentication, Model model,
        @RequestHeader(value = "condition", required = false) String condition,
        @RequestHeader(value = "touchable", required = false) Boolean touchable) {
        return genericIndex(authentication, model, touchable, decodes(condition, TagVO.class));
    }

    @PreAuthorize(Authorities.TAG_INDEX)
    @GetMapping("tags")
    public String items(Authentication authentication, Model model, TagVO condition) {
        return genericList(authentication, model, tagService.selectPageBy(condition), condition);
    }

    @PreAuthorize(Authorities.TAG_RETRIEVE)
    @GetMapping(value = "tag/{entityId}")
    public String retrieve(Authentication authentication, Model model, @PathVariable("entityId") Long entityId) {
        Tag item = null;
        if (entityId > 0) {
            item = tagService.selectById(entityId);
        }

        return genericEdit(authentication, model, item);
    }

    @PreAuthorize(Authorities.TAG_CREATE)
    @PostMapping(value = "tag", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> create(@Valid @RequestBody TagVO entity) throws ServiceException {
        return success(tagService.save(entity));
    }

    @PreAuthorize(Authorities.TAG_UPDATE)
    @PutMapping(value = "tag", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> update(@Valid @RequestBody TagVO entity) throws ServiceException {
        return success(tagService.save(entity));
    }

    @PreAuthorize(Authorities.TAG_DELETE)
    @DeleteMapping(value = "tag/{entityId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> delete(@PathVariable("entityId") Long entityId) throws ServiceException {
        return success(tagService.deleteById(entityId));
    }

}
