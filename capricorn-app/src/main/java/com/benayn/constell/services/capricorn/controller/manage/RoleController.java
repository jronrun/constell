package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.menu.MenuCapability;
import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.service.server.respond.Responds;
import com.benayn.constell.services.capricorn.repository.domain.Role;
import com.benayn.constell.services.capricorn.service.RoleService;
import com.benayn.constell.services.capricorn.config.Authorities;
import com.benayn.constell.services.capricorn.settings.constant.Menus;
import com.benayn.constell.services.capricorn.viewobject.RoleVo;
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
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = MANAGE_BASE)
public class RoleController extends BaseManageController<RoleVo> {

    private RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @MenuCapability(value = Menus.ROLE_MANAGE, parent = Menus.AUTHORIZATION)
    @PreAuthorize(Authorities.ROLE_INDEX)
    @GetMapping("role/index")
    public String index(Model model) {
        return genericIndex(model);
    }

    @PreAuthorize(Authorities.ROLE_INDEX)
    @GetMapping("roles")
    public String roles(Model model, RoleVo condition) {
        return genericList(model, roleService.selectPageBy(condition));
    }

    @PreAuthorize(Authorities.ROLE_RETRIEVE)
    @GetMapping(value = "role/{entityId}")
    public String retrieve(Model model, @PathVariable("entityId") Long entityId) {
        Role item = null;
        if (entityId > 0) {
            item = roleService.selectById(entityId);
        }

        return genericEdit(model, item);
    }

    @PreAuthorize(Authorities.ROLE_CREATE)
    @PostMapping(value = "role", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> create(@Valid @RequestBody RoleVo entity) throws ServiceException {
        return Responds.success(roleService.save(entity));
    }

    @PreAuthorize(Authorities.ROLE_UPDATE)
    @PutMapping(value = "role", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> update(@Valid @RequestBody RoleVo entity) throws ServiceException {
        return Responds.success(roleService.save(entity));
    }

    @PreAuthorize(Authorities.ROLE_DELETE)
    @DeleteMapping(value = "role/{entityId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> delete(@PathVariable("entityId") Long entityId) throws ServiceException {
        return Responds.success(roleService.deleteById(entityId));
    }

}
