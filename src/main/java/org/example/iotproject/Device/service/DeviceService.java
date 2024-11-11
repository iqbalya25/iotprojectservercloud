package org.example.iotproject.Device.service;

import org.example.iotproject.Device.entity.Device;

public interface DeviceService {
    Device getDeviceByName(String Name);
}
