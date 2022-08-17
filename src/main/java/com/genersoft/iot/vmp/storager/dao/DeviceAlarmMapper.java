package com.genersoft.iot.vmp.storager.dao;

import com.genersoft.iot.vmp.gb28181.bean.DeviceAlarm;
import com.genersoft.iot.vmp.gb28181.bean.DeviceChannel;
import com.genersoft.iot.vmp.vmanager.gb28181.platform.bean.ChannelReduce;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于存储设备的报警信息
 */
@Mapper
@Repository
public interface DeviceAlarmMapper {

    @Insert("INSERT INTO device_alarm (deviceId, channelId, alarmPriority, alarmMethod, alarmTime, alarmDescription, longitude, latitude, alarmType , createTime ) " +
            "VALUES ('${deviceId}', '${channelId}', '${alarmPriority}', '${alarmMethod}', '${alarmTime}', '${alarmDescription}', ${longitude}, ${latitude}, '${alarmType}', '${createTime}')")
    int add(DeviceAlarm alarm);


    @Select(value = {" <script>" +
            " SELECT * FROM device_alarm " +
            " WHERE 1=1 " +
            " <if test=\"deviceId != null\" >  AND deviceId = '${deviceId}'</if>" +
            " <if test=\"alarmPriority != null\" >  AND alarmPriority = '${alarmPriority}' </if>" +
            " <if test=\"alarmMethod != null\" >  AND alarmMethod = '${alarmMethod}' </if>" +
            " <if test=\"alarmType != null\" >  AND alarmType = '${alarmType}' </if>" +
            " <if test=\"startTime != null\" >  AND alarmTime &gt;= '${startTime}' </if>" +
            " <if test=\"endTime != null\" >  AND alarmTime &lt;= '${endTime}' </if>" +
            " ORDER BY alarmTime ASC " +
            " </script>"})
    List<DeviceAlarm> query(@Param("deviceId") String deviceId,
                            @Param("alarmPriority") String alarmPriority,
                            @Param("alarmMethod") String alarmMethod,
                            @Param("alarmType")String alarmType,
                            @Param("alarmType")String startTime,
                            @Param("endTime")String endTime);


    @Delete(" <script>" +
            "DELETE FROM device_alarm WHERE 1=1 " +
            " <if test=\"deviceIdList != null and id == null \" > AND deviceId in " +
            "<foreach collection='deviceIdList'  item='item'  open='(' separator=',' close=')' > '${item}'</foreach>" +
            "</if>" +
            " <if test=\"time != null and id == null \" > AND alarmTime &lt;= '${time}'</if>" +
            " <if test=\"id != null\" > AND id = ${id}</if>" +
            " </script>"
            )
    int clearAlarmBeforeTime(@Param("id")Integer id,
                             @Param("deviceIdList")List<String> deviceIdList,
                             @Param("time")String time);
}
