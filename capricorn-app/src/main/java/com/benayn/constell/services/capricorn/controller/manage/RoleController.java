package com.benayn.constell.services.capricorn.controller.manage;

import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.MANAGE_BASE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.benayn.constell.service.server.respond.Message;
import com.benayn.constell.service.server.respond.Responds;
import com.benayn.constell.services.capricorn.repository.RoleRepository;
import com.benayn.constell.services.capricorn.repository.domain.RoleExample;
import com.benayn.constell.services.capricorn.viewobject.RoleVo;
import com.google.common.collect.Maps;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = MANAGE_BASE)
public class RoleController extends BaseManageController<RoleVo> {

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("role/index")
    public String index(Model model) {
        return genericIndex(model);
    }

    @GetMapping(value = "roletest", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> roletest(Model model) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("search", getViewObjectResolver().getDefinedSearch(RoleVo.class, null));
        map.put("edit1", getViewObjectResolver().getDefinedEdit(RoleVo.class, roleRepository.selectById(1l)));
        map.put("page",
            getViewObjectResolver().getDefinedPage(RoleVo.class, roleRepository.selectPageBy(new RoleExample(), 1, 100)));

        return Responds.success(map);
    }

    @GetMapping("roles")
    public String roles(Model model) {
        return genericList(model, null);
    }

    @GetMapping(value = "role/{entityId}")
    public String retrieve(Model model, @PathVariable("entityId") Long entityId) {
        return genericEdit(model, null);
    }

    @PostMapping(value = "role", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> create(RoleVo roleVo) {
        return null;
    }

    @PutMapping(value = "role", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> update(RoleVo roleVo) {
        return null;
    }

    @DeleteMapping(value = "role/{entityId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> delete(@PathVariable("entityId") Long entityId) {
        return null;
    }

}
