package com.genersoft.iot.vmp.vmanager.user;

import com.genersoft.iot.vmp.conf.security.SecurityUtils;
import com.genersoft.iot.vmp.service.IMenuService;
import com.genersoft.iot.vmp.storager.dao.dto.Menu;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name  = "菜单管理")
@CrossOrigin
@RestController
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private IMenuService menuService;

    @GetMapping("/getMenuByRole")
    @Operation(summary = "查询用户菜单")
    public List<Menu> getMenuByRole() {
        int currenRoleId = SecurityUtils.getUserInfo().getRole().getId();
        System.out.println(SecurityUtils.encryptPassword("sugon@123"));
        return menuService.getMenuByRoleId(currenRoleId);
    }

    @GetMapping("/listAllMenus")
    @Operation(summary = "查询所有菜单")
    public List<Menu> listAllMenus() {
        return menuService.listAllMenus();
    }
}
