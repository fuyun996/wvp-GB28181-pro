package com.genersoft.iot.vmp.service.impl;

import com.genersoft.iot.vmp.gb28181.bean.Device;
import com.genersoft.iot.vmp.service.IOpenApiService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenApiServiceImpl implements IOpenApiService {
    @Override
    public List<Device> getDevicesList() {
        return null;
    }
}
