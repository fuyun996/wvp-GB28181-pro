package com.genersoft.iot.vmp.service.impl;

import com.genersoft.iot.vmp.service.IRoleMenuService;
import com.genersoft.iot.vmp.storager.dao.RoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleMenuServiceImpl implements IRoleMenuService {
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    /**
     * 为指定角色添加可访问的菜单，采用先删除在添加的处理方式
     * @param menuIds 可访问的菜单 id 数组
     * @param roleId 角色id
     */
    @Override
    @Transactional
    public void setMenuIdsByRole(int[] menuIds, int roleId) {
        if (roleMenuMapper.countMenuByRoleId(roleId) > 0) {
            roleMenuMapper.deleteMenuByRoleId(roleId);
        }
        roleMenuMapper.setMenuIdsByRole(menuIds, roleId);
    }
}
