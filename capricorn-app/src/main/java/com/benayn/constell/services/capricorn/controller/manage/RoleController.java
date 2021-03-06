package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.service.server.respond.Responds.success;
import static com.benayn.constell.service.util.LZString.decodes;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.service.server.respond.TouchRelation;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.service.RoleService;
import com.benayn.constell.services.capricorn.settings.constant.Menus;
import com.benayn.constell.services.capricorn.viewobject.RoleVO;
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
public class RoleController extends BaseManageController<RoleVO> {

    private RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @MenuCapability(value = Menus.ROLE_MANAGE, parent = Menus.AUTHORIZATION)
    @PreAuthorize(Authorities.MODEL_ROLE_INDEX)
    @GetMapping("role/index")
    public String index(Authentication authentication, Model model,
        @RequestHeader(value = "condition", required = false) String condition,
        @RequestHeader(value = "touchable", required = false) Boolean touchable) {
        return genericIndex(authentication, model, touchable, decodes(condition, RoleVO.class));
    }

    @PreAuthorize(Authorities.RELATION_ACCOUNT_ROLE_CREATE)
    @PostMapping(value = "role/relation", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> createRelation(@Valid @RequestBody TouchRelation relation) throws ServiceException {
        return success(roleService.createAccountRole(relation));
    }

    @PreAuthorize(Authorities.RELATION_ACCOUNT_ROLE_DELETE)
    @DeleteMapping(value = "role/relation", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> deleteRelation(@Valid @RequestBody TouchRelation relation) throws ServiceException {
        return success(roleService.deleteAccountRole(relation));
    }

    @PreAuthorize(Authorities.MODEL_ROLE_INDEX)
    @GetMapping("roles")
    public String roles(Authentication authentication, Model model, RoleVO condition) {
        return genericList(authentication, model, roleService.selectPageBy(condition), condition);
    }

    @PreAuthorize(Authorities.MODEL_ROLE_RETRIEVE)
    @GetMapping(value = "role/{entityId}")
    public String retrieve(Authentication authentication, Model model, @PathVariable("entityId") Long entityId) {
        Role item = null;
        if (entityId > 0) {
            item = roleService.selectById(entityId);
        }

        return genericEdit(authentication, model, item);
    }

    @PreAuthorize(Authorities.MODEL_ROLE_CREATE)
    @PostMapping(value = "role", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> create(@Valid @RequestBody RoleVO entity) throws ServiceException {
        return success(roleService.save(entity));
    }

    @PreAuthorize(Authorities.MODEL_ROLE_UPDATE)
    @PutMapping(value = "role", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> update(@Valid @RequestBody RoleVO entity) throws ServiceException {
        return success(roleService.save(entity));
    }

    @PreAuthorize(Authorities.MODEL_ROLE_DELETE)
    @DeleteMapping(value = "role/{entityId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> delete(@PathVariable("entityId") Long entityId) throws ServiceException {
        return success(roleService.deleteById(entityId));
    }

}
