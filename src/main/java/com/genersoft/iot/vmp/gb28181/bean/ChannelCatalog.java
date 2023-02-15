package com.genersoft.iot.vmp.gb28181.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.poi.ss.formula.functions.T;

/**
 * 设备自定义目录信息
 * @author lin
 */
@Schema(description = "设备自定义目录信息")
public class ChannelCatalog {
    @Schema(description = "ID")
    private String id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "设备国标编号")
    private String deviceId;

    @Schema(description = "父级目录ID")
    private String parentId;

    @Schema(description = "行政区划")
    private String civilCode;

    @Schema(description = "目录分组ID")
    private String catalogId;

    @Schema(description = "通道ID")
    private String channelId;

    @Schema(description = "用户ID")
    private Integer userId;

    private DeviceChannel basicData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCivilCode() {
        return civilCode;
    }

    public void setCivilCode(String civilCode) {
        this.civilCode = civilCode;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public DeviceChannel getBasicData() {
        return basicData;
    }

    public void setBasicData(DeviceChannel basicData) {
        this.basicData = basicData;
    }

}
