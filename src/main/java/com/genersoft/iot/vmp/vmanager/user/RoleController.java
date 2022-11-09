package com.genersoft.iot.vmp.vmanager.user;

import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.conf.security.SecurityUtils;
import com.genersoft.iot.vmp.gb28181.bean.DeviceChannel;
import com.genersoft.iot.vmp.service.*;
import com.genersoft.iot.vmp.storager.dao.dto.Menu;
import com.genersoft.iot.vmp.storager.dao.dto.Role;
import com.genersoft.iot.vmp.storager.dao.dto.RoleAuthorization;
import com.genersoft.iot.vmp.utils.DateUtil;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import com.genersoft.iot.vmp.vmanager.bean.WVPResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name  = "角色管理")
@CrossOrigin
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IRoleMenuService roleMenuService;

    @Autowired
    private IMenuService menuService;

    @Autowired
    private IDeviceChannelService deviceChannelService;

    @Autowired
    private IRoleDeviceChannelService roleDeviceChannelService;

    @PostMapping("/add")
    @Operation(summary = "添加角色")
    @Parameter(name = "name", description = "角色名", required = true)
    @Parameter(name = "authority", description = "权限（自行定义内容，目前未使用）", required = true)
    @PreAuthorize("hasAuthority('/roleManager')")
    public void add(@RequestParam String name,
                                                  @RequestParam(required = false) String authority){
        // 获取当前登录用户id
        int currenRoleId = SecurityUtils.getUserInfo().getRole().getId();
        if (currenRoleId != 1) {
            // 只用角色id为1才可以删除和添加用户
            throw new ControllerException(ErrorCode.ERROR403);
        }

        Role role = new Role();
        role.setName(name);
        role.setAuthority(authority);
        role.setCreateTime(DateUtil.getNow());
        role.setUpdateTime(DateUtil.getNow());

        int addResult = roleService.add(role);
        if (addResult <= 0) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除角色")
    @Parameter(name = "id", description = "用户Id", required = true)
    @PreAuthorize("hasAuthority('/roleManager')")
    public void delete(@RequestParam Integer id){
        // 获取当前登录用户id
        int currenRoleId = SecurityUtils.getUserInfo().getRole().getId();
        if (currenRoleId != 1) {
            // 只用角色id为0才可以删除和添加用户
            throw new ControllerException(ErrorCode.ERROR403);
        }
        int deleteResult = roleService.delete(id);

        if (deleteResult <= 0) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
    }

    @PostMapping("/update")
    @Operation(summary = "修改角色")
    @Parameter(name = "id", description = "角色id", required = true)
    @Parameter(name = "name", description = "角色名称", required = true)
    @Parameter(name = "authority", description = "角色权限编码", required = false)
    @PreAuthorize("hasAuthority('/roleManager')")
    public void update(@RequestParam int id,
                       @RequestParam String name,
                       @RequestParam(required = false) String authority) {

        Role role = new Role();
        role.setId(id);
        role.setName(name);
        role.setAuthority(authority);
        role.setUpdateTime(DateUtil.getNow());

        int updateResult = roleService.update(role);

        if (updateResult <= 0) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
    }

    @GetMapping("/all")
    @Operation(summary = "查询角色")
    @PreAuthorize("hasAuthority('/roleManager')")
    public List<Role> all(){
        // 获取当前登录用户id
        List<Role> allRoles = roleService.getAll();
        return roleService.getAll();
    }

    @PostMapping("/setRoleMenuAuthority")
    @Operation(summary = "设置角色菜单权限")
    @PreAuthorize("hasAuthority('/roleManager')")
    public void setRoleMenuAuthority(@RequestBody RoleAuthorization roleAuthorization) {
        roleMenuService.setMenuIdsByRole(roleAuthorization.getMenuIds(), roleAuthorization.getRoleId());
    }

    @PostMapping("/setRoleChannelAuthority")
    @Operation(summary = "设置角色通道权限")
    @PreAuthorize("hasAuthority('/roleManager')")
    public void setRoleChannelAuthority(@RequestBody RoleAuthorization roleAuthorization) {
        roleDeviceChannelService.setChannelIdsByRole(roleAuthorization.getChannelIds(), roleAuthorization.getRoleId());
    }

    @GetMapping("/getMenuByRole")
    @Operation(summary = "查询角色菜单")
    public List<Menu> getMenuByRole(@RequestParam(required = false) Integer roleId) {
        if (roleId == null) {
            roleId = SecurityUtils.getUserInfo().getRole().getId();
        }

        return menuService.getMenuByRoleId(roleId);
    }

    @GetMapping("/getChannelByRole")
    @Operation(summary = "查询角色通道")
    public List<DeviceChannel> getChannelByRole(@RequestParam(required = false) Integer roleId) {
        if (roleId == null) {
            roleId = SecurityUtils.getUserInfo().getRole().getId();
        }

        return deviceChannelService.getChannelByRoleId(roleId);
    }
}
