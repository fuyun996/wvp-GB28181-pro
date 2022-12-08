package com.genersoft.iot.vmp.service.impl;

import com.genersoft.iot.vmp.service.IRoleDeviceChannelService;
import com.genersoft.iot.vmp.storager.dao.RoleDeviceChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    /**
     * 为指定角色添加可访问的通道
     * @param channelIds 可访问的通道 id 数组
     * @param roleId 角色id
     */
    @Override
    @Transactional
    public void setChannelIdsByRole(String[] channelIds, int roleId) {
        if (roleDeviceChannelMapper.countChannelByRoleId(roleId) > 0) {
            roleDeviceChannelMapper.deleteByRoleId(roleId);
        }

        roleDeviceChannelMapper.setChannelIdsByRole(channelIds, roleId);
    }

    @Override
    public Map getDeviceChannelByRoleIdAndChannelId(int roleId, String channelid) {
        return roleDeviceChannelMapper.getDeviceChannelByRoleIdAndChannelId(roleId, channelid);
    }
}
