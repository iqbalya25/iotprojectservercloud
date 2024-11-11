package org.example.iotproject.Device.repository;

import org.example.iotproject.Device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device findByDeviceName(String name);
}
