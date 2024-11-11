package org.example.iotproject.Device.service.impl;

import org.example.iotproject.Device.entity.Device;
import org.example.iotproject.Device.repository.DeviceRepository;
import org.example.iotproject.Device.service.DeviceService;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Device getDeviceByName(String Name) {
        return deviceRepository.findByDeviceName(Name);
    }
}
