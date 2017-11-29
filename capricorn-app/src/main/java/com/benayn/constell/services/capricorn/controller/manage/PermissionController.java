package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.service.server.respond.Responds.success;
import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.service.server.respond.TouchRelation;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.repository.domain.Permission;
import com.benayn.constell.services.capricorn.service.PermissionService;
import com.benayn.constell.services.capricorn.settings.constant.Menus;
import com.benayn.constell.services.capricorn.viewobject.PermissionVo;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class PermissionController extends BaseManageController<PermissionVo> {

    private PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @MenuCapability(value = Menus.PERMISSION_MANAGE, parent = Menus.AUTHORIZATION)
    @PreAuthorize(Authorities.PERMISSION_INDEX)
    @GetMapping("permission/index")
    public String index(Model model, @RequestHeader(value = "touchable", required = false) Boolean touchable) {
        return genericIndex(model, touchable);
    }

    @PreAuthorize(Authorities.RELATION_ROLE_PERMISSION_CREATE)
    @PostMapping(value = "permission/relation", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> createRelation(@Valid @RequestBody TouchRelation relation) throws ServiceException {
        return success(permissionService.createRolePermission(relation));
    }

    @PreAuthorize(Authorities.RELATION_ROLE_PERMISSION_DELETE)
    @DeleteMapping(value = "permission/relation", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> deleteRelation(@Valid @RequestBody TouchRelation relation) throws ServiceException {
        return success(permissionService.deleteRolePermission(relation));
    }

    @PreAuthorize(Authorities.PERMISSION_INDEX)
    @GetMapping("permissions")
    public String permissions(Model model, PermissionVo condition) {
        return genericList(model, permissionService.selectPageBy(condition), condition);
    }

    @PreAuthorize(Authorities.PERMISSION_RETRIEVE)
    @GetMapping(value = "permission/{entityId}")
    public String retrieve(Model model, @PathVariable("entityId") Long entityId) {
        Permission item = null;
        if (entityId > 0) {
            item = permissionService.selectById(entityId);
        }

        return genericEdit(model, item);
    }

    @PreAuthorize(Authorities.PERMISSION_CREATE)
    @PostMapping(value = "permission", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> create() throws ServiceException {
        return success(permissionService.saveFromAuthorities());
    }

    @PreAuthorize(Authorities.PERMISSION_UPDATE)
    @PutMapping(value = "permission", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> update(@Valid @RequestBody PermissionVo entity) throws ServiceException {
        return success(permissionService.save(entity));
    }

    @PreAuthorize(Authorities.PERMISSION_DELETE)
    @DeleteMapping(value = "permission/{entityId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> delete(@PathVariable("entityId") Long entityId) throws ServiceException {
        return success(permissionService.deleteById(entityId));
    }

}
