package com.genersoft.iot.vmp.storager.dao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "菜单信息封装对象")
public class Menu {
    @Schema(description = "菜单id")
    private Integer id;

    @Schema(description = "父菜单id")
    private Integer pid;
    @Schema(description = "菜单名称")
    private String name;
    @Schema(description = "菜单url")
    private String url;

    @Schema(description = "菜单顺序，数字越大排序越靠后")
    private Integer sort;

    @Schema(description = "菜单是否展开, 0-不展开, 1-展开")
    private Integer spread;
    @Schema(description = "菜单是否可用 0-不可以, 1-可以用")
    private Integer status;
    @Schema(description = "菜单创建时间")
    private String createTime;
    @Schema(description = "菜单修改时间")
    private String updateTime;

    private List<Menu> child;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getSpread() {
        return spread;
    }

    public void setSpread(Integer spread) {
        this.spread = spread;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public List<Menu> getChild() {
        return child;
    }

    public void setChild(List<Menu> child) {
        this.child = child;
    }
}
