package com.genersoft.iot.vmp.service;

import com.genersoft.iot.vmp.storager.dao.dto.Menu;

import java.util.List;

public interface IMenuService {

    /**
     * 更具角色获取其对应的菜单
     * @param roleId 角色id
     * @return 当前角色所拥有的菜单
     */
    List<Menu> getMenuByRoleId(int roleId);

    /**
     * 查询系统的所有菜单
     * @return 系统的所有菜单
     */
    List<Menu> listAllMenus();
}
