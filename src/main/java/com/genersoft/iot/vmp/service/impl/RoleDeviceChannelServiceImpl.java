package com.genersoft.iot.vmp.service.impl;

import com.genersoft.iot.vmp.service.IRoleDeviceChannelService;
import com.genersoft.iot.vmp.storager.dao.RoleDeviceChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleDeviceChannelServiceImpl implements IRoleDeviceChannelService {
    @Autowired
    private RoleDeviceChannelMapper roleDeviceChannelMapper;
    /**
     * 获取指定角色所能访问的通道
     * @param roleId 角色id
     * @return 通道列表
     */
    @Override
    public List<String> getDeviceChannelByRoleId(int roleId) {
        return roleDeviceChannelMapper.getDeviceChannelByRoleId(roleId);
    }
}
