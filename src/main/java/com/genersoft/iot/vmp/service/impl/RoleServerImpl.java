package com.genersoft.iot.vmp.service.impl;

import com.genersoft.iot.vmp.service.IRoleService;
import com.genersoft.iot.vmp.storager.dao.RoleDeviceChannelMapper;
import com.genersoft.iot.vmp.storager.dao.RoleMapper;
import com.genersoft.iot.vmp.storager.dao.RoleMenuMapper;
import com.genersoft.iot.vmp.storager.dao.dto.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServerImpl implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private RoleDeviceChannelMapper roleDeviceChannelMapper;

    @Override
    public Role getRoleById(int id) {
        return roleMapper.selectById(id);
    }

    @Override
    public int add(Role role) {
        return roleMapper.add(role);
    }

    @Override
    @Transactional
    public int delete(int id) {
        int deleteResult = roleMapper.delete(id);
        // 删除角色与菜单的关联
        roleMenuMapper.deleteMenuByRoleId(id);
        // 删除角色与通道的关联
        roleDeviceChannelMapper.deleteByRoleId(id);
        return deleteResult;
    }

    @Override
    public List<Role> getAll() {
        return roleMapper.selectAll();
    }

    @Override
    public int update(Role role) {
        return roleMapper.update(role);
    }
}
