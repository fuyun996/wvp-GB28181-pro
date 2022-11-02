package com.genersoft.iot.vmp.service;


public interface IRoleMenuService {
    /**
     * 为指定角色添加可访问的菜单
     * @param menuIds 可访问的菜单 id 数组
     * @param roleId 角色id
     */
    void setMenuIdsByRole(int[] menuIds, int roleId);
}
