package com.genersoft.iot.vmp.service.impl;

import com.genersoft.iot.vmp.conf.DynamicTask;
import com.genersoft.iot.vmp.gb28181.bean.Device;
import com.genersoft.iot.vmp.gb28181.transmit.cmd.ISIPCommander;
import com.genersoft.iot.vmp.gb28181.transmit.event.request.impl.message.response.cmd.CatalogResponseMessageHandler;
import com.genersoft.iot.vmp.service.IDeviceService;
import com.genersoft.iot.vmp.gb28181.task.impl.CatalogSubscribeTask;
import com.genersoft.iot.vmp.gb28181.task.impl.MobilePositionSubscribeTask;
import com.genersoft.iot.vmp.gb28181.bean.SyncStatus;
import com.genersoft.iot.vmp.storager.IRedisCatchStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 设备业务（目录订阅）
 */
@Service
public class DeviceServiceImpl implements IDeviceService {

    private final static Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    private DynamicTask dynamicTask;

    @Autowired
    private ISIPCommander sipCommander;

    @Autowired
    private CatalogResponseMessageHandler catalogResponseMessageHandler;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Override
    public boolean addCatalogSubscribe(Device device) {
        if (device == null || device.getSubscribeCycleForCatalog() < 0) {
            return false;
        }
        if (dynamicTask.contains(device.getDeviceId() + "catalog")) {
            // 存在则停止现有的，开启新的
            dynamicTask.stop(device.getDeviceId() + "catalog");
        }
        logger.info("[添加目录订阅] 设备{}", device.getDeviceId());
        // 添加目录订阅
        CatalogSubscribeTask catalogSubscribeTask = new CatalogSubscribeTask(device, sipCommander);
        catalogSubscribeTask.run();
        // 提前开始刷新订阅
        int subscribeCycleForCatalog = device.getSubscribeCycleForCatalog();
        // 设置最小值为30
        subscribeCycleForCatalog = Math.max(subscribeCycleForCatalog, 30);
        dynamicTask.startCron(device.getDeviceId() + "catalog", catalogSubscribeTask, subscribeCycleForCatalog);
        return true;
    }

    @Override
    public boolean removeCatalogSubscribe(Device device) {
        if (device == null || device.getSubscribeCycleForCatalog() < 0) {
            return false;
        }
        logger.info("移除目录订阅: {}", device.getDeviceId());
        dynamicTask.stop(device.getDeviceId() + "catalog");
        return true;
    }

    @Override
    public boolean addMobilePositionSubscribe(Device device) {
        if (device == null || device.getSubscribeCycleForMobilePosition() < 0) {
            return false;
        }
        if (dynamicTask.contains(device.getDeviceId() + "mobile_position")) {
            // 存在则停止现有的，开启新的
            dynamicTask.stop(device.getDeviceId() + "mobile_position");
        }
        logger.info("[添加移动位置订阅] 设备{}", device.getDeviceId());
        // 添加目录订阅
        MobilePositionSubscribeTask mobilePositionSubscribeTask = new MobilePositionSubscribeTask(device, sipCommander);
        mobilePositionSubscribeTask.run();
        // 提前开始刷新订阅
        int subscribeCycleForCatalog = device.getSubscribeCycleForCatalog();
        // 设置最小值为30
        subscribeCycleForCatalog = Math.max(subscribeCycleForCatalog, 30);
        dynamicTask.startCron(device.getDeviceId() + "mobile_position" , mobilePositionSubscribeTask, subscribeCycleForCatalog -1 );
        return true;
    }

    @Override
    public boolean removeMobilePositionSubscribe(Device device) {
        if (device == null || device.getSubscribeCycleForCatalog() < 0) {
            return false;
        }
        logger.info("移除移动位置订阅: {}", device.getDeviceId());
        dynamicTask.stop(device.getDeviceId() + "mobile_position");
        return true;
    }

    @Override
    public SyncStatus getChannelSyncStatus(String deviceId) {
        return catalogResponseMessageHandler.getChannelSyncProgress(deviceId);
    }

    @Override
    public void setChannelSyncReady(String deviceId) {
        catalogResponseMessageHandler.setChannelSyncReady(deviceId);
    }

    @Override
    public void setChannelSyncEnd(String deviceId, String errorMsg) {
        catalogResponseMessageHandler.setChannelSyncEnd(deviceId, errorMsg);
    }
}
