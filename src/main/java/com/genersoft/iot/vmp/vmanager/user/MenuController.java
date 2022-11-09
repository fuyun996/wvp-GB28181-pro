package com.genersoft.iot.vmp.vmanager.user;

import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.conf.security.SecurityUtils;
import com.genersoft.iot.vmp.service.IMenuService;
import com.genersoft.iot.vmp.storager.dao.dto.Menu;
import com.genersoft.iot.vmp.utils.DateUtil;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name  = "菜单管理")
@CrossOrigin
@RestController
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private IMenuService menuService;

    @PostMapping("/add")
    @Operation(summary = "添加菜单")
    @Secured("ROLE_admin") // 只有admin角色可调用
    public void add(@RequestBody Menu menu) {
        menu.setStatus(1);
        menu.setCreateTime(DateUtil.getNow());
        menu.setUpdateTime(DateUtil.getNow());

        int addResult = menuService.add(menu);
        if (addResult <= 0) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除菜单")
    @Parameter(name = "menuId", description = "菜单id", required = true)
    @Secured("ROLE_admin") // 只有admin角色可调用
    public void delete(int menuId) {

        int deleteResult = menuService.delete(menuId);

        if (deleteResult <= 0) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
    }

    @PostMapping("/update")
    @Operation(summary = "修改菜单")
    @Secured("ROLE_admin") // 只有admin角色可调用
    public void update(@RequestBody Menu menu) {
        menu.setUpdateTime(DateUtil.getNow());
        int updateResult = menuService.updateById(menu);
        if (updateResult <= 0) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
    }

    @GetMapping("/listAllMenus")
    @Operation(summary = "查询所有菜单")
    @Secured("ROLE_admin") // 只有admin角色可调用
    public List<Menu> listAllMenus() {
        return menuService.listAllMenus();
    }
}
