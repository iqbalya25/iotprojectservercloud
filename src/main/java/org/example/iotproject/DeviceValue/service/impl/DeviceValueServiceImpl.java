package org.example.iotproject.DeviceValue.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.DeviceValue.entity.DeviceValue;
import org.example.iotproject.DeviceValue.repository.DeviceValueRepository;
import org.example.iotproject.DeviceValue.service.DeviceValueService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeviceValueServiceImpl implements DeviceValueService {
    private final DeviceValueRepository deviceValueRepository;

    public DeviceValueServiceImpl(DeviceValueRepository deviceValueRepository) {
        this.deviceValueRepository = deviceValueRepository;
    }

    @Override
    public DeviceValue saveDeviceValue(DeviceValue deviceValue) {
        try {
            return deviceValueRepository.save(deviceValue);
        } catch (Exception e) {
            log.error("Error saving device value", e);
            throw e;
        }
    }
}
