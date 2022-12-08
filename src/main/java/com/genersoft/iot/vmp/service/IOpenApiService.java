package com.genersoft.iot.vmp.service;

import com.genersoft.iot.vmp.gb28181.bean.Device;

import java.util.List;

public interface IOpenApiService {
    public List<Device> getDevicesList();

}
