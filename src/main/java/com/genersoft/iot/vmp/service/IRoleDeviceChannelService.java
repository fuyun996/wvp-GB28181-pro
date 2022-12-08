package com.genersoft.iot.vmp.service;

import java.util.List;
import java.util.Map;

public interface IRoleDeviceChannelService {
    /**
     * 获取指定角色所能访问的通道
     * @param roleId 角色id
     * @return 通道列表
     */
    List<String> getDeviceChannelByRoleId(int roleId);

    /**
     * 为指定角色添加可访问的通道
     * @param channelIds 可访问的通道 id 数组
     * @param roleId 角色id
     */
    void setChannelIdsByRole(String[] channelIds, int roleId);

    Map getDeviceChannelByRoleIdAndChannelId(int roleId, String channelid);
}
