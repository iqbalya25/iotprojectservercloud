package org.example.iotproject.DeviceStatus.repository;

import org.example.iotproject.DeviceStatus.entity.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceStatusRepository extends JpaRepository<DeviceStatus, Long> {
}
