package com.genersoft.iot.vmp.service;

import java.util.List;

public interface IRoleDeviceChannelService {
    /**
     * 获取指定角色所能访问的通道
     * @param roleId 角色id
     * @return 通道列表
     */
    List<String> getDeviceChannelByRoleId(int roleId);
}
