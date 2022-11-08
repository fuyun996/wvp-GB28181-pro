package com.genersoft.iot.vmp.storager.dao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "角色信息封装对象")
public class Role {

    @Schema(description = "角色id")
    private int id;
    @Schema(description = "角色名称")
    private String name;
    @Schema(description = "权限编码")
    private String authority;
    @Schema(description = "角色创建时间")
    private String createTime;
    @Schema(description = "角色修改时间")
    private String updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
