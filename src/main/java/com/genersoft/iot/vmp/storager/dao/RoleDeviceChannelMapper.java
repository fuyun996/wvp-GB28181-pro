package com.genersoft.iot.vmp.storager.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface RoleDeviceChannelMapper {
    @Select("<script>" + "select channelId from role_device_channel " +
            "<where>" +
                "<if test=\"roleId != 1\">" +
                    "role_id=#{roleId}" +
                "</if>" +
            "</where>" +
            "</script>")
    List<String> getDeviceChannelByRoleId(int roleId);

    @Delete("delete from role_device_channel where channelId in (select dev.channelId from device_channel dev where dev.deviceId=#{deviceId})")
    int delete(String deviceId);

    @Select("select count(*) from role_device_channel where role_id=#{roleId}")
    int countChannelByRoleId(int roleId);

    @Delete("delete from role_device_channel where role_id=#{roleId}")
    int deleteByRoleId(int roleId);

    @Insert("<script>" +
            "insert into role_device_channel(role_id, channelId) values" +
            "<foreach collection=\"channelIds\" separator=\",\" item=\"item\">(#{roleId},#{item})</foreach>" +
            "</script>")
    void setChannelIdsByRole(@Param("channelIds") String[] channelIds, int roleId);

    @Select("select * from role_device_channel where role_id=#{roleId} and channelId=#{channelid}")
    Map getDeviceChannelByRoleIdAndChannelId(int roleId, String channelid);
}
