package com.genersoft.iot.vmp.storager.dao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@Schema(description = "角色权限封装对象")
public class RoleAuthorization {
    @Schema(description = "角色id")
    @NotNull
    private Integer roleId;
    @Schema(description = "菜单权限")
    private int[] menuIds;
    @Schema(description = "通道权限")
    private String[] channelIds;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public int[] getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(int[] menuIds) {
        this.menuIds = menuIds;
    }

    public String[] getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(String[] channelIds) {
        this.channelIds = channelIds;
    }
}
