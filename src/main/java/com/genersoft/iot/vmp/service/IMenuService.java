package com.genersoft.iot.vmp.service;

import com.genersoft.iot.vmp.storager.dao.dto.Menu;

import java.util.List;

public interface IMenuService {

    /**
     * 添加新菜单
     * @param menu 菜单对象
     * @return 1 操作成功 0 操作失败
     */
    int add(Menu menu);

    /**
     * 删除菜单
     * @param menuId 待删除菜单的id
     * @return 1 操作成功 0 操作失败
     */
    int delete(int menuId);

    /**
     * 修改菜单信息
     * @param menu 菜单信息
     * @return 1 操作成功 0 操作失败
     */
    int updateById(Menu menu);

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
