package org.example.iotproject.DeviceStatus.service.impl;

import org.example.iotproject.DeviceStatus.entity.DeviceStatus;
import org.example.iotproject.DeviceStatus.repository.DeviceStatusRepository;
import org.example.iotproject.DeviceStatus.service.DeviceStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceStatusServiceImpl  implements DeviceStatusService {
    private DeviceStatusRepository deviceStatusRepository;

    @Autowired
    public DeviceStatusServiceImpl(DeviceStatusRepository deviceStatusRepository) {
        this.deviceStatusRepository = deviceStatusRepository;
    }

    @Override
    public String saveDeviceStatus(DeviceStatus deviceStatus) {
        deviceStatusRepository.save(deviceStatus);
        return ("Device status saved successfully.");
    }
}
