package com.genersoft.iot.vmp.service.impl;

import com.genersoft.iot.vmp.service.IMenuService;
import com.genersoft.iot.vmp.storager.dao.MenuMapper;
import com.genersoft.iot.vmp.storager.dao.RoleMenuMapper;
import com.genersoft.iot.vmp.storager.dao.dto.Menu;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuServiceImpl implements IMenuService {
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    /**
     * 添加新菜单
     * @param menu 菜单对象
     * @return 1 操作成功 0 操作失败
     */
    @Override
    public int add(Menu menu) {
        return menuMapper.add(menu);
    }

    /**
     * 删除菜单
     * @param menuId 待删除菜单的id
     * @return 1 操作成功 0 操作失败
     */
    @Override
    @Transactional
    public int delete(int menuId) {
        int deleteMenu = menuMapper.delete(menuId);
        // 删除角色与此菜单关联的记录
        roleMenuMapper.deleteMenuByMenuId(menuId);
        return deleteMenu;
    }

    /**
     * 更具角色获取其对应的菜单
     * @param roleId 角色id
     * @return 当前角色所拥有的菜单
     */
    @Override
    @Parameter(name = "roleId", description = "角色ID", required = true)
    public List<Menu> getMenuByRoleId(int roleId) {
        return menuMapper.getMenuByRoleId(roleId);
    }

    /**
     * 查询系统的所有菜单
     * @return 系统的所有菜单
     */
    @Override
    public List<Menu> listAllMenus() {
        return menuMapper.listAllMenus();
    }
}
