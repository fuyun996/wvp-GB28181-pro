package com.genersoft.iot.vmp.storager.dao;

import com.genersoft.iot.vmp.storager.dao.dto.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoleDeviceChannelMapper {
    @Select("select channelId from role_device_channel where role_id=#{roleId}")
    List<String> getDeviceChannelByRoleId(int roleId);
}
